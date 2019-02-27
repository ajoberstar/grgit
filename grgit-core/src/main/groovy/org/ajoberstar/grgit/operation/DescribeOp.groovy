package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.service.ResolveService
import org.eclipse.jgit.api.DescribeCommand

/**
 * Find the nearest tag reachable. Returns an {@link String}}.
 * @see <a href="http://ajoberstar.org/grgit/grgit-describe.html">grgit-describe</a>
 * @see <a href="http://git-scm.com/docs/git-describe">git-describe Manual Page</a>
 */
@Operation('describe')
class DescribeOp implements Callable<String> {
  private final Repository repo

  DescribeOp(Repository repo){
    this.repo = repo
  }

  /**
   * Sets the commit to be described. Defaults to HEAD.
   * @see {@link ResolveService#toRevisionString(Object)}
   */
  Object commit

  /**
   * Whether to show a uniquely abbreviated commit if no tags match.
   */
  boolean always

  /**
   * Whether to always use long output format or not.
   */
  boolean longDescr

  /**
   * Include non-annotated tags when determining nearest tag.
   */
  boolean tags

  /**
   * glob patterns to match tags against before they are considered
   */
  List<String> match = []

  String call(){
    DescribeCommand cmd = repo.jgit.describe()
    if (commit == null) {
      commit = 'HEAD'
    }
    ResolveService resolver = new ResolveService(repo)
    cmd.setTarget(resolver.toRevisionString(commit))
    cmd.setLong(longDescr)
    cmd.setTags(tags)
    if (match) {
      cmd.setMatch(match as String[])
    }
    String description = cmd.call()
    return description == null && always ? resolver.toCommit(commit).abbreviatedId : description
  }
}
