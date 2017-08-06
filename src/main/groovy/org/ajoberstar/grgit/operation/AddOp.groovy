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
import org.eclipse.jgit.api.AddCommand

/**
 * Adds files to the index.
 * @since 0.1.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-add.html">grgit-add</a>
 * @see <a href="http://git-scm.com/docs/git-add">git-add Manual Page</a>
 */
@Operation('add')
class AddOp implements Callable<Void> {
  private final Repository repo

  /**
   * Patterns of files to add to the index.
   */
  Set<String> patterns = []

  /**
   * {@code true} if changes to all currently tracked files should be added
   * to the index, {@code false} otherwise.
   */
  boolean update = false

  AddOp(Repository repo) {
    this.repo = repo
  }

  Void call() {
    AddCommand cmd = repo.jgit.add()
    patterns.each { cmd.addFilepattern(it) }
    cmd.update = update
    cmd.call()
    return null
  }
}
