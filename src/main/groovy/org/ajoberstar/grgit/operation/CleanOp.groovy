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
import org.eclipse.jgit.api.CleanCommand

/**
 * Remove untracked files from the working tree. Returns the list of
 * file paths deleted.
 * @since 0.2.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-clean.html">grgit-clean</a>
 * @see <a href="http://git-scm.com/docs/git-clean">git-clean Manual Page</a>
 */
@Operation('clean')
class CleanOp implements Callable<Set<String>> {
  private final Repository repo

  /**
   * The paths to clean. {@code null} if all paths should be included.
   */
  Set<String> paths

  /**
   * {@code true} if untracked directories should also be deleted,
   * {@code false} (the default) otherwise
   */
  boolean directories = false

  /**
   * {@code true} if the files should be returned, but not deleted,
   * {@code false} (the default) otherwise
   */
  boolean dryRun = false

  /**
   * {@code false} if files ignored by {@code .gitignore} should
   * also be deleted, {@code true} (the default) otherwise
   */
  boolean ignore = true

  CleanOp(Repository repo) {
    this.repo = repo
  }

  Set<String> call() {
    CleanCommand cmd = repo.jgit.clean()
    if (paths) { cmd.paths = paths }
    cmd.cleanDirectories = directories
    cmd.dryRun = dryRun
    cmd.ignore = ignore

    return cmd.call()
  }
}
