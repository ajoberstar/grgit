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

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.auth.TransportOpUtil
import org.ajoberstar.grgit.internal.Operation
import org.eclipse.jgit.api.PullCommand
import org.eclipse.jgit.api.PullResult

/**
 * Pulls changes from the remote on the current branch. If the changes
 * conflict, the pull will fail, any conflicts can be retrieved with
 * {@code grgit.status()}, and throwing an exception.
 * @since 0.2.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-pull.html">grgit-pull</a>
 * @see <a href="http://git-scm.com/docs/git-pull">git-pull Manual Page</a>
 */
@Operation('pull')
class PullOp implements Callable<Void> {
  private final Repository repo

  /**
   * The name of the remote to pull. If not set, the current branch's
   * configuration will be used.
   */
  String remote

  /**
   * The name of the remote branch to pull. If not set, the current branch's
   * configuration will be used.
   */
  String branch

  /**
   * Rebase on top of the changes when they are pulled in, if
   * {@code true}. {@code false} (the default) otherwise.
   */
  boolean rebase = false

  PullOp(Repository repo) {
    this.repo = repo
  }

  Void call() {
    PullCommand cmd = repo.jgit.pull()
    if (remote) { cmd.remote = remote }
    if (branch) { cmd.remoteBranchName = branch }
    cmd.rebase = rebase
    TransportOpUtil.configure(cmd, repo.credentials)

      PullResult result = cmd.call()
      if (!result.successful) {
        throw new IllegalStateException("Could not pull: ${result}")
      }
      return null
  }
}
