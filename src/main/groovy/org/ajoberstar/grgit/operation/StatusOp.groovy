/*
 * Copyright 2012-2015 the original author or authors.
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
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.api.StatusCommand
import org.eclipse.jgit.api.errors.GitAPIException

/**
 * Gets the current status of the repository. Returns an {@link Status}.
 *
 * <p>Get the current status.</p>
 *
 * <pre>
 * def status = grgit.status()
 * </pre>
 *
 * See <a href="http://git-scm.com/docs/git-status">git-status Manual Page</a>.
 *
 * @since 0.1.0
 * @see <a href="http://git-scm.com/docs/git-status">git-status Manual Page</a>
 */
class StatusOp implements Callable<Status> {
  private final Repository repo

  StatusOp(Repository repo) {
    this.repo = repo
  }

  Status call() {
    StatusCommand cmd = repo.jgit.status()
    try {
      return JGitUtil.convertStatus(cmd.call())
    } catch (GitAPIException e) {
      throw new GrgitException('Problem retrieving status.', e)
    }
  }
}
