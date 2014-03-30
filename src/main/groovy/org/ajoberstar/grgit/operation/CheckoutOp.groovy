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

/**
 * Checks out a branch to the working tree. Does not support checking out
 * specific paths.
 *
 * <p>To checkout an existing branch.</p>
 *
 * <pre>
 * grgit.checkout(branch: 'existing-branch')
 * grgit.checkout(branch: 'existing-branch', createBranch: false)
 * </pre>
 *
 * <p>To checkout a new branch starting at, but not tracking, the current HEAD.</p>
 *
 * <pre>
 * grgit.checkout(branch: 'new-branch', createBranch: true)
 * </pre>
 *
 * <p>To checkout a new branch starting at, but not tracking, a start point.</p>
 *
 * <pre>
 * grgit.checkout(branch: 'new-branch', startPoint: 'any-branch', createBranch: true)
 * </pre>
 *
 * See <a href="http://git-scm.com/docs/git-checkout">git-checkout Manual Page</a>.
 *
 * @since 0.1.0
 * @see <a href="http://git-scm.com/docs/git-checkout">git-checkout Manual Page</a>
 */
class CheckoutOp implements Callable<Void> {
	private final Repository repo

	/**
	 * The branch or commit to checkout.
	 */
	String branch

	/**
	 * {@code true} if the branch does not exist and should be created,
	 * {@code false} (the default) otherwise
	 */
	boolean createBranch = false

	/**
	 * If {@code createBranch} is {@code true}, start the new branch
	 * at this commit.
	 */
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
