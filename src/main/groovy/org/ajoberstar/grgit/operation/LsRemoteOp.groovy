package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Ref
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.auth.TransportOpUtil
import org.ajoberstar.grgit.internal.Operation
import org.eclipse.jgit.api.LsRemoteCommand
import org.eclipse.jgit.lib.ObjectId

/**
 * List references in a remote repository.
 * @since 2.0.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-lsremote.html">grgit-lsremote</a>
 * @see <a href="https://git-scm.com/docs/git-ls-remote">git-ls-remote Manual Page</a>
 */
@Operation('lsremote')
class LsRemoteOp implements Callable<Map<Ref, String>> {
  private final Repository repo

  String remote = 'origin'

  boolean heads = false

  boolean tags = false

  LsRemoteOp(Repository repo) {
    this.repo = repo
  }

  Map<Ref, String> call() {
    LsRemoteCommand cmd = repo.jgit.lsRemote()
    TransportOpUtil.configure(cmd, repo.credentials)
    cmd.remote = remote
    cmd.heads = heads
    cmd.tags = tags
    return cmd.call().collectEntries { jgitRef ->
      Ref ref = new Ref(jgitRef.getName())
      [(ref): ObjectId.toString(jgitRef.getObjectId())]
    }.asImmutable()
  }
}
