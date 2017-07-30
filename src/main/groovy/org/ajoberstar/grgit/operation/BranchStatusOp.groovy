/*
 * Copyright 2012-2017 the original author or authors.
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
import org.ajoberstar.grgit.BranchStatus
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.service.ResolveService
import org.eclipse.jgit.lib.BranchTrackingStatus

/**
 * Gets the tracking status of a branch. Returns a {@link BranchStatus}.
 *
 * <pre>
 * def status = grgit.branch.status(name: 'the-branch')
 * </pre>
 *
 * @since 0.2.0
 */
@Operation('status')
class BranchStatusOp implements Callable<BranchStatus> {
  private final Repository repo

  /**
   * The branch to get the status of.
   * @see {@link ResolveService#toBranch(Object)}
   */
  Object name

  BranchStatusOp(Repository repo) {
    this.repo = repo
  }

  BranchStatus call() {
    Branch realBranch = new ResolveService(repo).toBranch(name)
    if (realBranch.trackingBranch) {
      BranchTrackingStatus status = BranchTrackingStatus.of(repo.jgit.repository, realBranch.fullName)
      if (status) {
        return new BranchStatus(realBranch, status.aheadCount, status.behindCount)
      } else {
        throw new IllegalStateException("Could not retrieve status for ${name}")
      }
    } else {
      throw new IllegalStateException("${name} is not set to track another branch")
    }
  }
}
