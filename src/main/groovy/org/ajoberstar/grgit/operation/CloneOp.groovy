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

import org.ajoberstar.grgit.Credentials
import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.auth.TransportOpUtil
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.util.CoercionUtil
import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.Git

/**
 * Clones an existing repository. Returns a {@link Grgit} pointing
 * to the resulting repository.
 * @since 0.1.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-clone.html">grgit-clone</a>
 * @see <a href="http://git-scm.com/docs/git-clone">git-clone Manual Reference.</a>
 */
@Operation('clone')
class CloneOp implements Callable<Grgit> {
  /**
   * The directory to put the cloned repository.
   * @see {@link CoercionUtil#toFile(Object)}
   */
  Object dir

  /**
   * The URI to the repository to be cloned.
   */
  String uri

  /**
   * The name of the remote for the upstream repository. Defaults
   * to {@code origin}.
   */
  String remote = 'origin'

  /**
   * {@code true} if the resulting repository should be bare,
   * {@code false} (the default) otherwise.
   */
  boolean bare = false

  /**
   * {@code true} (the default) if a working tree should be checked out,
   * {@code false} otherwise
   */
  boolean checkout = true

  /**
   * The remote ref that should be checked out after the repository is
   * cloned. Defaults to {@code master}.
   */
  String refToCheckout

  /**
   * The username and credentials to use when checking out the
   * repository and for subsequent remote operations on the
   * repository. This is only needed if hardcoded creds
   * should be used.
   * @see {@link org.ajoberstar.gradle.auth.AuthConfig}
   */
  Credentials credentials

  Grgit call() {
    if (!checkout && refToCheckout) {
      throw new IllegalArgumentException('Cannot specify a refToCheckout and set checkout to false.')
    }

    CloneCommand cmd = Git.cloneRepository()
    TransportOpUtil.configure(cmd, credentials)

    cmd.directory = CoercionUtil.toFile(dir)
    cmd.uri = uri
    cmd.remote = remote
    cmd.bare = bare
    cmd.noCheckout = !checkout
    if (refToCheckout) { cmd.branch = refToCheckout }

    Git jgit = cmd.call()
    Repository repo = new Repository(CoercionUtil.toFile(dir), jgit, credentials)
    return new Grgit(repo)
  }
}
