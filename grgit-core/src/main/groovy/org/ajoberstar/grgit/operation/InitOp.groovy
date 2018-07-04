package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.util.CoercionUtil
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.InitCommand

/**
 * Initializes a new repository. Returns a {@link Grgit} pointing
 * to the resulting repository.
 * @since 0.1.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-init.html">grgit-init</a>
 * @see <a href="http://git-scm.com/docs/git-init">git-init Manual Reference.</a>
 */
@Operation('init')
class InitOp implements Callable<Grgit> {
  /**
   * {@code true} if the repository should not have a
   * working tree, {@code false} (the default) otherwise
   */
  boolean bare = false

  /**
   * The directory to initialize the repository in.
   * @see {@link CoercionUtil#toFile(Object)}
   */
  Object dir

  Grgit call() {
    InitCommand cmd = Git.init()
    cmd.bare = bare
    cmd.directory = CoercionUtil.toFile(dir)
    Git jgit = cmd.call()
    Repository repo = new Repository(CoercionUtil.toFile(dir), jgit, null)
    return new Grgit(repo)
  }
}
