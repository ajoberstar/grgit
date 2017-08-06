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
import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.util.JGitUtil
import org.eclipse.jgit.api.StatusCommand

/**
 * Gets the current status of the repository. Returns an {@link Status}.
 * @since 0.1.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-status.html">grgit-status</a>
 * @see <a href="http://git-scm.com/docs/git-status">git-status Manual Page</a>
 */
@Operation('status')
class StatusOp implements Callable<Status> {
  private final Repository repo

  StatusOp(Repository repo) {
    this.repo = repo
  }

  Status call() {
    StatusCommand cmd = repo.jgit.status()
    return JGitUtil.convertStatus(cmd.call())
  }
}
