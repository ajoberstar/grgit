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
