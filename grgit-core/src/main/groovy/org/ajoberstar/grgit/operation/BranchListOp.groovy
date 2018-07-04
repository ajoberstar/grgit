package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Branch
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.service.ResolveService
import org.ajoberstar.grgit.util.JGitUtil
import org.eclipse.jgit.api.ListBranchCommand

/**
 * Lists branches in the repository. Returns a list of {@link Branch}.
 * @since 0.2.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-branch.html">grgit-branch</a>
 * @see <a href="http://git-scm.com/docs/git-branch">git-branch Manual Page</a>
 */
@Operation('list')
class BranchListOp implements Callable<List<Branch>> {
  private final Repository repo

  /**
   * Which branches to return.
   */
  Mode mode = Mode.LOCAL

  /**
   * Commit ref branches must contains
   */
  Object contains = null

  BranchListOp(Repository repo) {
    this.repo = repo
  }

  List<Branch> call() {
    ListBranchCommand cmd = repo.jgit.branchList()
    cmd.listMode = mode.jgit
    if (contains) {
      cmd.contains = new ResolveService(repo).toRevisionString(contains)
    }
    return cmd.call().collect {
      JGitUtil.resolveBranch(repo, it.name)
    }
  }

  static enum Mode {
    ALL(ListBranchCommand.ListMode.ALL),
    REMOTE(ListBranchCommand.ListMode.REMOTE),
    LOCAL(null)

    private final ListBranchCommand.ListMode jgit

    private Mode(ListBranchCommand.ListMode jgit) {
      this.jgit = jgit
    }
  }
}
