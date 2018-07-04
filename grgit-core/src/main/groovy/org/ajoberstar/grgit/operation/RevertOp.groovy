package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.service.ResolveService
import org.ajoberstar.grgit.util.JGitUtil
import org.eclipse.jgit.api.RevertCommand
import org.eclipse.jgit.revwalk.RevCommit

/**
 * Revert one or more commits. Returns the new HEAD {@link Commit}.
 * @since 0.1.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-revert.html">grgit-revert</a>
 * @see <a href="http://git-scm.com/docs/git-revert">git-revert Manual Page</a>
 */
@Operation('revert')
class RevertOp implements Callable<Commit> {
  private final Repository repo

  /**
   * List of commits to revert.
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  List<Object> commits = []

  RevertOp(Repository repo) {
    this.repo = repo
  }

  Commit call() {
    RevertCommand cmd = repo.jgit.revert()
    commits.each {
      String revstr = new ResolveService(repo).toRevisionString(it)
      cmd.include(JGitUtil.resolveObject(repo, revstr))
    }
    RevCommit commit = cmd.call()
    if (cmd.failingResult) {
      throw new IllegalStateException("Could not merge reverted commits (conflicting files can be retrieved with a call to grgit.status()): ${cmd.failingResult}")
    }
    return JGitUtil.convertCommit(repo, commit)
  }
}
