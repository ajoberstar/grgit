package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.eclipse.jgit.api.RmCommand

/**
 * Remove files from the index and (optionally) delete them from the working tree.
 * Note that wildcards are not supported.
 * @since 0.1.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-remove.html">grgit-remove</a>
 * @see <a href="http://git-scm.com/docs/git-rm">git-rm Manual Page</a>
 */
@Operation('remove')
class RmOp implements Callable<Void> {
  private final Repository repo

  /**
   * The file patterns to remove.
   */
  Set<String> patterns = []

  /**
   * {@code true} if files should only be removed from the index,
   * {@code false} (the default) otherwise.
   */
  boolean cached = false

  RmOp(Repository repo) {
    this.repo = repo
  }

  Void call() {
    RmCommand cmd = repo.jgit.rm()
    patterns.each { cmd.addFilepattern(it) }
    cmd.cached = cached
    cmd.call()
    return null
  }
}
