package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.Tag
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.util.JGitUtil
import org.eclipse.jgit.api.ListTagCommand

/**
 * Lists tags in the repository. Returns a list of {@link Tag}.
 * @since 0.2.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-tag.html">grgit-tag</a>
 * @see <a href="http://git-scm.com/docs/git-tag">git-tag Manual Page</a>
 */
@Operation('list')
class TagListOp implements Callable<List<Tag>> {
  private final Repository repo

  TagListOp(Repository repo) {
    this.repo = repo
  }

  List<Tag> call() {
    ListTagCommand cmd = repo.jgit.tagList()

    return cmd.call().collect {
      JGitUtil.resolveTag(repo, it)
    }
  }
}
