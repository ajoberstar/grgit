package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.eclipse.jgit.api.SubmoduleInitCommand
import org.eclipse.jgit.api.SubmoduleUpdateCommand

import java.util.concurrent.Callable

/**
 * FSynchronization of the submodule status with the declared one.
 * @since 5.0.0
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
        SubmoduleInitCommand initCommand = repo.jgit.submoduleInit()
        initCommand.call()
        SubmoduleUpdateCommand updateCmd = repo.jgit.submoduleUpdate()
        updateCmd.call()
        return null
    }
}
