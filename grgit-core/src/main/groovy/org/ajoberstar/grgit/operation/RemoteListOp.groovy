package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.Remote
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.util.JGitUtil
import org.eclipse.jgit.transport.RemoteConfig

/**
 * Lists remotes in the repository. Returns a list of {@link org.ajoberstar.grgit.Remote}.
 * @see <a href="http://ajoberstar.org/grgit/grgit-remote.html">grgit-remote</a>
 * @see <a href="http://git-scm.com/docs/git-remote">git-remote Manual Page</a>
 */
@Operation('list')
class RemoteListOp implements Callable<List<Remote>> {
  private final Repository repository

  RemoteListOp(Repository repo) {
    this.repository = repo
  }

  @Override
  List<Remote> call() {
    return RemoteConfig.getAllRemoteConfigs(repository.jgit.repository.config).collect { rc ->
      if (rc.uris.size() > 1 || rc.pushURIs.size() > 1) {
        throw new IllegalArgumentException("Grgit does not currently support multiple URLs in remote: [uris: ${rc.uris}, pushURIs:${rc.pushURIs}]")
      }
      JGitUtil.convertRemote(rc)
    }
  }
}
