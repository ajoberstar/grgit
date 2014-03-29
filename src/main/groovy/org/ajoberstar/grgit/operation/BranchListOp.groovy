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

import org.ajoberstar.grgit.Branch
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.api.errors.GitAPIException

class BranchListOp implements Callable<List<Branch>> {
	private final Repository repo

	Mode mode = Mode.LOCAL

	BranchListOp(Repository repo) {
		this.repo = repo
	}

	List<Branch> call() {
		ListBranchCommand cmd = repo.jgit.branchList()
		cmd.listMode = mode.jgit

		try {
			return cmd.call().collect {
				JGitUtil.resolveBranch(repo, it.name)
			}
		} catch (GitAPIException e) {
			throw new GrgitException('Problem listing branches.', e)
		}
	}

	static enum Mode {
		ALL(ListBranchCommand.ListMode.ALL),
		REMOTE(ListBranchCommand.ListMode.REMOTE),
		LOCAL(null)

		private final ListBranchCommand.ListMode jgit

		private Mode(ListBranchCommand.ListMode jgit) {
			this.jgit = jgit
		}
	}
}
