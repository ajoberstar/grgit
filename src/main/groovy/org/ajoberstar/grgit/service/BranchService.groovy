package org.ajoberstar.grgit.service

import org.ajoberstar.grgit.Branch
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.WithOperations
import org.ajoberstar.grgit.operation.*
import org.ajoberstar.grgit.util.JGitUtil
import org.eclipse.jgit.lib.Ref

/**
 * Provides support for performing branch-related operations on
 * a Git repository.
 *
 * <p>
 *   Details of each operation's properties and methods are available on the
 *   doc page for the class. The following operations are supported directly on
 *   this service instance.
 * </p>
 *
 * <ul>
 *   <li>{@link org.ajoberstar.grgit.operation.BranchAddOp add}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.BranchChangeOp change}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.BranchListOp list}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.BranchRemoveOp remove}</li>
 *   <li>{@link org.ajoberstar.grgit.operation.BranchStatusOp status}</li>
 * </ul>
 *
 * @since 0.2.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-branch.html">grgit-branch</a>
 */
@WithOperations(instanceOperations=[BranchListOp, BranchAddOp, BranchRemoveOp, BranchChangeOp, BranchStatusOp])
class BranchService {
  private final Repository repository

  BranchService(Repository repository) {
    this.repository = repository
  }

  /**
   * Gets the branch associated with the current HEAD.
   * @return the branch or {@code null} if the HEAD is detached
   */
  Branch current() {
    Ref ref = repository.jgit.repository.exactRef('HEAD')?.target
    return ref ? JGitUtil.resolveBranch(repository, ref) : null
  }

  /**
   * Gets the branch associated with the current HEAD.
   * @return the branch or {@code null} if the HEAD is detached
   * @deprecated Use BranchService#current()
   */
  @Deprecated
  Branch getCurrent() {
    return current()
  }
}
