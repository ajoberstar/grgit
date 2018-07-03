package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.util.JGitUtil
import org.eclipse.jgit.api.StatusCommand

/**
 * Gets the current status of the repository. Returns an {@link Status}.
 * @since 0.1.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-status.html">grgit-status</a>
 * @see <a href="http://git-scm.com/docs/git-status">git-status Manual Page</a>
 */
@Operation('status')
class StatusOp implements Callable<Status> {
  private final Repository repo

  StatusOp(Repository repo) {
    this.repo = repo
  }

  Status call() {
    StatusCommand cmd = repo.jgit.status()
    return JGitUtil.convertStatus(cmd.call())
  }
}
