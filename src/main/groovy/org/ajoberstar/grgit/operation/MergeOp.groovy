/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.api.MergeCommand
import org.eclipse.jgit.api.MergeResult
import org.eclipse.jgit.api.errors.GitAPIException

class MergeOp implements Callable<Void> {
	private final Repository repo

	String head
	Mode mode

	MergeOp(Repository repo) {
		this.repo = repo
	}

	Void call() {
		MergeCommand cmd = repo.jgit.merge()
		if (head) { cmd.include(JGitUtil.resolveObject(repo, head)) }
		switch (mode) {
			case Mode.ONLY_FF:
				cmd.fastForward = MergeCommand.FastForwardMode.FF_ONLY
				break
			case Mode.CREATE_COMMIT:
				cmd.fastForward = MergeCommand.FastForwardMode.NO_FF
				break
			case Mode.SQUASH:
				cmd.squash = true
				break
			case Mode.NO_COMMIT:
				cmd.commit = false
				break
		}

		try {
			MergeResult result = cmd.call()
			if (!result.mergeStatus.successful) {
				throw new GrgitException("Could not merge: ${result}")
			}
			return null
		} catch (GitAPIException e) {
			throw new GrgitException('Problem merging.', e)
		}
	}

	static enum Mode {
		DEFAULT,
		ONLY_FF,
		CREATE_COMMIT,
		SQUASH,
		NO_COMMIT
	}
}
