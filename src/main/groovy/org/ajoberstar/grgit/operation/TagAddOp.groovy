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

import org.ajoberstar.grgit.Person
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.Tag
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.service.ResolveService
import org.ajoberstar.grgit.util.JGitUtil
import org.eclipse.jgit.api.TagCommand
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.lib.Ref

/**
 * Adds a tag to the repository. Returns the newly created {@link Tag}.
 * @since 0.2.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-tag.html">grgit-tag</a>
 * @see <a href="http://git-scm.com/docs/git-tag">git-tag Manual Page</a>
 */
@Operation('add')
class TagAddOp implements Callable<Tag> {
  private final Repository repo

  /**
   * The name of the tag to create.
   */
  String name

  /**
   * The message to put on the tag.
   */
  String message

  /**
   * The person who created the tag.
   */
  Person tagger

  /**
   * {@code true} (the default) if an annotated tag should be
   * created, {@code false} otherwise.
   */
  boolean annotate = true

  /**
   * {@code true} to overwrite an existing tag, {@code false}
   * (the default) otherwise
   */
  boolean force = false

  /**
   * The commit the tag should point to.
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  Object pointsTo

  TagAddOp(Repository repo) {
    this.repo = repo
  }

  Tag call() {
    TagCommand cmd = repo.jgit.tag()
    cmd.name = name
    cmd.message = message
    if (tagger) { cmd.tagger = new PersonIdent(tagger.name, tagger.email) }
    cmd.annotated = annotate
    cmd.forceUpdate = force
    if (pointsTo) {
      def revstr = new ResolveService(repo).toRevisionString(pointsTo)
      cmd.objectId = JGitUtil.resolveRevObject(repo, revstr)
    }

    Ref ref = cmd.call()
    return JGitUtil.resolveTag(repo, ref)
  }
}
