package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.eclipse.jgit.api.CleanCommand

/**
 * Remove untracked files from the working tree. Returns the list of
 * file paths deleted.
 * @since 0.2.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-clean.html">grgit-clean</a>
 * @see <a href="http://git-scm.com/docs/git-clean">git-clean Manual Page</a>
 */
@Operation('clean')
class CleanOp implements Callable<Set<String>> {
  private final Repository repo

  /**
   * The paths to clean. {@code null} if all paths should be included.
   */
  Set<String> paths

  /**
   * {@code true} if untracked directories should also be deleted,
   * {@code false} (the default) otherwise
   */
  boolean directories = false

  /**
   * {@code true} if the files should be returned, but not deleted,
   * {@code false} (the default) otherwise
   */
  boolean dryRun = false

  /**
   * {@code false} if files ignored by {@code .gitignore} should
   * also be deleted, {@code true} (the default) otherwise
   */
  boolean ignore = true

  CleanOp(Repository repo) {
    this.repo = repo
  }

  Set<String> call() {
    CleanCommand cmd = repo.jgit.clean()
    if (paths) { cmd.paths = paths }
    cmd.cleanDirectories = directories
    cmd.dryRun = dryRun
    cmd.ignore = ignore

    return cmd.call()
  }
}
