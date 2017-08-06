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
import org.eclipse.jgit.api.PushCommand

/**
 * Push changes to a remote repository.
 * @since 0.1.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-push.html">grgit-push</a>
 * @see <a href="http://git-scm.com/docs/git-push">git-push Manual Page</a>
 */
@Operation('push')
class PushOp implements Callable<Void> {
  private final Repository repo

  /**
   * The remote to push to.
   */
  String remote

  /**
   * The refs or refspecs to use when pushing. If {@code null}
   * and {@code all} is {@code false} only push the current branch.
   */
  List refsOrSpecs = []

  /**
   * {@code true} to push all branches, {@code false} (the default)
   * to only push the current one.
   */
  boolean all = false

  /**
   * {@code true} to push tags, {@code false} (the default) otherwise.
   */
  boolean tags = false

  /**
   * {@code true} if branches should be pushed even if they aren't
   * a fast-forward, {@code false} (the default) if it should fail.
   */
  boolean force = false

  /**
   * {@code true} if result of this operation should be just estimation
   * of real operation result, no real push is performed.
   * {@code false} (the default) if real push to remote repo should be performed.
   *
   * @since 0.4.1
   */
  boolean dryRun = false

  PushOp(Repository repo) {
    this.repo = repo
  }

  Void call() {
    PushCommand cmd = repo.jgit.push()
    TransportOpUtil.configure(cmd, repo.credentials)
    if (remote) { cmd.remote = remote }
    refsOrSpecs.each { cmd.add(it) }
    if (all) { cmd.setPushAll() }
    if (tags) { cmd.setPushTags() }
    cmd.force = force
    cmd.dryRun = dryRun
    cmd.call()
    return null
  }
}
