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
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.service.ResolveService
import org.ajoberstar.grgit.util.JGitUtil
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode
import org.eclipse.jgit.lib.Ref

/**
 * Adds a branch to the repository. Returns the newly created {@link Branch}.
 * @since 0.2.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-branch.html">grgit-branch</a>
 * @see <a href="http://git-scm.com/docs/git-branch">git-branch Manual Page</a>
 */
@Operation('add')
class BranchAddOp implements Callable<Branch> {
  private final Repository repo

  /**
   * The name of the branch to add.
   */
  String name

  /**
   * The commit the branch should start at. If this is a remote branch
   * it will be automatically tracked.
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  Object startPoint

  /**
   * The tracking mode to use. If {@code null}, will use the default
   * behavior.
   */
  Mode mode

  BranchAddOp(Repository repo) {
    this.repo = repo
  }

  Branch call() {
    if (mode && !startPoint) {
      throw new IllegalStateException('Cannot set mode if no start point.')
    }
    CreateBranchCommand cmd = repo.jgit.branchCreate()
    cmd.name = name
    cmd.force = false
    if (startPoint) {
      String rev = new ResolveService(repo).toRevisionString(startPoint)
      cmd.startPoint = rev
    }
    if (mode) { cmd.upstreamMode = mode.jgit }

    Ref ref = cmd.call()
    return JGitUtil.resolveBranch(repo, ref)
  }

  static enum Mode {
    TRACK(SetupUpstreamMode.TRACK),
    NO_TRACK(SetupUpstreamMode.NOTRACK)

    private final SetupUpstreamMode jgit

    Mode(SetupUpstreamMode jgit) {
      this.jgit = jgit
    }
  }
}
