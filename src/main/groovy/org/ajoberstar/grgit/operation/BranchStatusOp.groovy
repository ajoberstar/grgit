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

import java.io.IOException
import java.util.concurrent.Callable

import org.ajoberstar.grgit.Branch
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.BranchStatus
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.lib.BranchTrackingStatus

class BranchStatusOp implements Callable<BranchStatus> {
	private final Repository repo

	String branch

	BranchStatusOp(Repository repo) {
		this.repo = repo
	}

	BranchStatus call() {
		try {
			Branch realBranch = JGitUtil.resolveBranch(repo, branch)
			if (realBranch.trackingBranch) {
				BranchTrackingStatus status = BranchTrackingStatus.of(repo.git.repository, realBranch.fullName)
				if (status) {
					return new BranchStatus(realBranch, status.aheadCount, status.behindCount)
				} else {
					throw new GrgitException("Could not retrieve status for ${branch}")
				}
			} else {
				throw new GrgitException("${branch} is not set to track another branch")
			}
		} catch (IOException e) {
			throw new GrgitException('Problem retrieving branch status.', e)
		}
	}
}
