package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.auth.TransportOpUtil
import org.ajoberstar.grgit.internal.Operation
import org.eclipse.jgit.api.FetchCommand
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.transport.TagOpt

/**
 * Fetch changes from remotes.
 * @since 0.2.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-fetch.html">grgit-fetch</a>
 * @see <a href="http://git-scm.com/docs/git-fetch">git-fetch Manual Reference.</a>
 */
@Operation('fetch')
class FetchOp implements Callable<Void> {
  private final Repository repo

  /**
   * Which remote should be fetched.
   */
  String remote

  /**
   * List of refspecs to fetch.
   */
  List refSpecs = []

  /**
   * {@code true} if branches removed by the remote should be
   * removed locally.
   */
  boolean prune = false

  /**
   * The depth of the clone. Defaults to full history.
   */
  Integer depth = null

  /**
   * How should tags be handled.
   */
  TagMode tagMode = TagMode.AUTO

  FetchOp(Repository repo) {
    this.repo = repo
  }

  /**
   * Provides a string conversion to the enums.
   */
  void setTagMode(String mode) {
    tagMode = mode.toUpperCase()
  }

  Void call() {
    FetchCommand cmd = repo.jgit.fetch()
    TransportOpUtil.configure(cmd, repo.credentials)
    if (remote) { cmd.remote = remote }
    cmd.refSpecs = refSpecs.collect { new RefSpec(it) }
    cmd.removeDeletedRefs = prune
    cmd.tagOpt = tagMode.jgit
    if (depth) { cmd.depth = depth }
    cmd.call()
    return null
  }

  enum TagMode {
    AUTO(TagOpt.AUTO_FOLLOW),
    ALL(TagOpt.FETCH_TAGS),
    NONE(TagOpt.NO_TAGS)

    final TagOpt jgit

    private TagMode(TagOpt opt) {
      this.jgit = opt
    }
  }
}
