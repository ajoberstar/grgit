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
import org.ajoberstar.grgit.util.CoercionUtil
import org.eclipse.jgit.api.ApplyCommand

/**
 * Apply a patch to the index.
 * @since 0.1.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-apply.html">grgit-apply</a>
 * @see <a href="http://git-scm.com/docs/git-apply">git-apply Manual Page</a>
 */
@Operation('apply')
class ApplyOp implements Callable<Void> {
  private final Repository repo

  /**
   * The patch file to apply to the index.
   * @see {@link CoercionUtil#toFile(Object)}
   */
  Object patch

  ApplyOp(Repository repo) {
    this.repo = repo
  }

  Void call() {
    ApplyCommand cmd = repo.jgit.apply()
    if (!patch) {
      throw new IllegalStateException('Must set a patch file.')
    }
    CoercionUtil.toFile(patch).withInputStream { stream ->
      cmd.patch = stream
      cmd.call()
      return null
    }
  }
}
