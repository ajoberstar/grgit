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
import org.eclipse.jgit.api.DeleteTagCommand

/**
 * Removes one or more tags from the repository. Returns a list of
 * the fully qualified tag names that were removed.
 * @since 0.2.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-tag.html">grgit-tag</a>
 * @see <a href="http://git-scm.com/docs/git-tag">git-tag Manual Page</a>
 */
@Operation('remove')
class TagRemoveOp implements Callable<List<String>> {
  private final Repository repo

  /**
   * Names of tags to remove.
   * @see {@link ResolveService#toTagName(Object)}
   */
  List names = []

  TagRemoveOp(Repository repo) {
    this.repo = repo
  }

  List<String> call() {
    DeleteTagCommand cmd = repo.jgit.tagDelete()
    cmd.tags = names.collect { new ResolveService(repo).toTagName(it) }

    return cmd.call()
  }
}
