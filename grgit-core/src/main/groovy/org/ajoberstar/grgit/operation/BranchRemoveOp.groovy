package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.service.ResolveService
import org.eclipse.jgit.api.DeleteBranchCommand

/**
 * Removes one or more branches from the repository. Returns a list of
 * the fully qualified branch names that were removed.
 * @since 0.2.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-branch.html">grgit-branch</a>
 * @see <a href="http://git-scm.com/docs/git-branch">git-branch Manual Page</a>
 */
@Operation('remove')
class BranchRemoveOp implements Callable<List<String>> {
  private final Repository repo

  /**
   * List of all branche names to remove.
   * @see {@link ResolveService#toBranchName(Object)}
   */
  List names = []

  /**
   * If {@code false} (the default), only remove branches that
   * are merged into another branch. If {@code true} will delete
   * regardless.
   */
  boolean force = false

  BranchRemoveOp(Repository repo) {
    this.repo = repo
  }

  List<String> call() {
    DeleteBranchCommand cmd = repo.jgit.branchDelete()
    cmd.branchNames = names.collect { new ResolveService(repo).toBranchName(it) }
    cmd.force = force

    return cmd.call()
  }
}
