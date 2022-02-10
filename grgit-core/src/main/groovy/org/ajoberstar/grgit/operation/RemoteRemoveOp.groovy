package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.eclipse.jgit.lib.Config

/**
 * Removes a remote from the repository.
 * @see <a href="http://ajoberstar.org/grgit/grgit-remote.html">grgit-remote</a>
 * @see <a href="http://git-scm.com/docs/git-remote">git-remote Manual Page</a>
 */
@Operation('remove')
class RemoteRemoveOp implements Callable<Void> {

    private final Repository repository

    /**
     * Name of the remote.
     */
    String name

    RemoteRemoveOp(Repository repo) {
        this.repository = repo
    }

    @Override
    Void call() {
        Config config = repository.jgit.repository.config
        config.unsetSection("remote", name)
        config.save();
    }
}
