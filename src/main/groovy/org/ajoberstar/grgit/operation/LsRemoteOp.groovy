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

import org.ajoberstar.grgit.Ref
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.eclipse.jgit.api.LsRemoteCommand
import org.eclipse.jgit.lib.ObjectId

/**
 * Gets a log of commits in the repository. Returns a list of {@link Commit}s.
 * Since a Git history is not necessarilly a line, these commits may not be in
 * a strict order.
 *
 * <p>Get a full log of commits from the current HEAD and back.</p>
 *
 * <pre>
 * def history = grgit.log()
 * </pre>
 *
 * <p>Get log of commits between two points.</p>
 *
 * <pre>
 * def history = grgit.log {
 *  range 'v1.0', 'v2.0'
 * }
 * </pre>
 *
 * <p>Get a list of the most recent 5 commits.</p>
 *
 * <pre>
 * def history = grgit.log(maxCommits: 5)
 * </pre>
 *
 * <p>Get a log of commits skipping the most recent 3.</p>
 *
 * <pre>
 * def history = grgit.log(skipCommits: 3)
 * </pre>
 *
 * See <a href="https://git-scm.com/docs/git-ls-remote">git-ls-remote Manual Page</a>.
 *
 * @since 2.0.0
 * @see <a href="https://git-scm.com/docs/git-ls-remote">git-ls-remote Manual Page</a>
 */
@Operation('lsremote')
class LsRemoteOp implements Callable<Map<Ref, String>> {
  private final Repository repo

  String remote = 'origin'

  boolean heads = false

  boolean tags = false

  LsRemoteOp(Repository repo) {
    this.repo = repo
  }

  Map<Ref, String> call() {
    LsRemoteCommand cmd = repo.jgit.lsRemote()
    cmd.remote = remote
    cmd.heads = heads
    cmd.tags = tags
    return cmd.call().collectEntries { jgitRef ->
      Ref ref = new Ref(jgitRef.getName())
      [(ref): ObjectId.toString(jgitRef.getObjectId())]
    }.asImmutable()
  }
}
