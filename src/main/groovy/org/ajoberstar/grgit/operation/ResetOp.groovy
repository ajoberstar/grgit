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
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.service.ResolveService
import org.eclipse.jgit.api.ResetCommand

/**
 * Reset changes in the repository.
 * @since 0.1.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-reset.html">grgit-reset</a>
 * @see <a href="http://git-scm.com/docs/git-reset">git-reset Manual Page</a>
 */
@Operation('reset')
class ResetOp implements Callable<Void> {
  private final Repository repo

  /**
   * The paths to reset.
   */
  Set<String> paths = []

  /**
   * The commit to reset back to. Defaults to HEAD.
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  Object commit

  /**
   * The mode to use when resetting.
   */
  Mode mode = Mode.MIXED

  ResetOp(Repository repo) {
    this.repo = repo
  }

  void setMode(String mode) {
    this.mode = mode.toUpperCase()
  }

  Void call() {
    if (!paths.empty && mode != Mode.MIXED) {
      throw new IllegalStateException('Cannot set mode when resetting paths.')
    }

    ResetCommand cmd = repo.jgit.reset()
    paths.each { cmd.addPath(it) }
    if (commit) {
      cmd.ref = new ResolveService(repo).toRevisionString(commit)
    }
    if (paths.empty) {
      cmd.mode = mode.jgit
    }

    cmd.call()
    return null
  }

  static enum Mode {
    /**
     * Reset the index and working tree.
     */
    HARD(ResetCommand.ResetType.HARD),
    /**
     * Reset the index, but not the working tree.
     */
    MIXED(ResetCommand.ResetType.MIXED),
    /**
     * Only reset the HEAD. Leave the index and working tree as-is.
     */
    SOFT(ResetCommand.ResetType.SOFT)

    private final ResetCommand.ResetType jgit

    private Mode(ResetCommand.ResetType jgit) {
      this.jgit = jgit
    }
  }
}
