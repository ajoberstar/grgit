package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.eclipse.jgit.api.SubmoduleInitCommand
import org.eclipse.jgit.api.SubmoduleSyncCommand
import org.eclipse.jgit.api.SubmoduleUpdateCommand
import org.eclipse.jgit.submodule.SubmoduleStatus

import java.util.concurrent.Callable

/**
 * Synchronization of the submodule status with the declared one.
 * @since 5.1.0
 * @see <a href="https://git-scm.com/docs/git-submodule">git-submodule Manual Reference.</a>
 */
@Operation('submodule')
class UpdateSubmodulesOp implements Callable<Void> {
    private final Repository repo

    UpdateSubmodulesOp(Repository repo) {
        this.repo = repo
    }

    @Override
    Void call() throws Exception {
        repo.jgit.submoduleInit().call()
        repo.jgit.submoduleUpdate().call()
        repo.jgit.submoduleSync().call()
        return null
    }
}
