package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.service.ResolveService
import org.eclipse.jgit.api.CheckoutCommand

/**
 * Checks out a branch to the working tree. Does not support checking out
 * specific paths.
 * @since 0.1.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-checkout.html">grgit-checkout</a>
 * @see <a href="http://git-scm.com/docs/git-checkout">git-checkout Manual Page</a>
 */
@Operation('checkout')
class CheckoutOp implements Callable<Void> {
  private final Repository repo

  /**
   * The branch or commit to checkout.
    * @see {@link ResolveService#toBranchName(Object)}
   */
  Object branch

  /**
   * {@code true} if the branch does not exist and should be created,
   * {@code false} (the default) otherwise
   */
  boolean createBranch = false

  /**
   * If {@code createBranch} or {@code orphan} is {@code true}, start the new branch
   * at this commit.
    * @see {@link ResolveService#toRevisionString(Object)}
   */
  Object startPoint

  /**
   * {@code true} if the new branch is to be an orphan,
   * {@code false} (the default) otherwise
   */
  boolean orphan = false

  CheckoutOp(Repository repo) {
    this.repo = repo
  }

  Void call() {
    if (startPoint && !createBranch && !orphan) {
      throw new IllegalArgumentException('Cannot set a start point if createBranch and orphan are false.')
    } else if ((createBranch || orphan) && !branch) {
      throw new IllegalArgumentException('Must specify branch name to create.')
    }
    CheckoutCommand cmd = repo.jgit.checkout()
    ResolveService resolve = new ResolveService(repo)
    if (branch) { cmd.name = resolve.toBranchName(branch) }
    cmd.createBranch = createBranch
    cmd.startPoint = resolve.toRevisionString(startPoint)
    cmd.orphan = orphan
    cmd.call()
    return null
  }
}
