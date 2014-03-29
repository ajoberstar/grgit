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

import org.eclipse.jgit.api.CheckoutCommand
import org.eclipse.jgit.api.errors.GitAPIException

class CheckoutOp implements Callable<Void> {
	private final Repository repo

	String branch
	boolean createBranch = false
	String startPoint

	CheckoutOp(Repository repo) {
		this.repo = repo
	}

	Void call() {
		if (startPoint && !createBranch) {
			throw new IllegalArgumentException('Cannot set a start point if createBranch is false.')
		} else if (createBranch && !branch) {
			throw new IllegalArgumentException('Must specify branch name to create.')
		}
		CheckoutCommand cmd = repo.jgit.checkout()
		if (branch) { cmd.name = branch }
		cmd.createBranch = createBranch
		cmd.startPoint = startPoint
		try {
			cmd.call()
			return null
		} catch (GitAPIException e) {
			throw new GrgitException('Problem checking out.', e)
		}
	}
}
