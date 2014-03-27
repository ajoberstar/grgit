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

import org.eclipse.jgit.api.PullCommand
import org.eclipse.jgit.api.PullResult
import org.eclipse.jgit.api.errors.GitAPIException

class PullOp implements Callable<Void> {
	private final Repository repo

	boolean rebase = false

	PullOp(Repository repo) {
		this.repo = repo
	}

	Void call() {
		PullCommand cmd = repo.git.pull()
		cmd.rebase = rebase

		try {
			PullResult result = cmd.call()
			println result
			if (!result.successful) {
				throw new GrgitException("Could not pull: ${result}")
			}
			return null
		} catch (GitAPIException e) {
			throw new GrgitException('Problem merging.', e)
		}
	}
}
