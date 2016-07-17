/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.service.ResolveService

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Branch
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.api.errors.GitAPIException

/**
 * Lists branches in the repository. Returns a list of {@link Branch}.
 *
 * <p>To list local branches only.</p>
 *
 * <pre>
 * def branches = grgit.branch.list()
 * def branches = grgit.branch.list(mode: BranchListOp.Mode.LOCAL)
 * </pre>
 *
 * <p>To list remote branches only.</p>
 *
 * <pre>
 * def branches = grgit.branch.list(mode: BranchListOp.Mode.REMOTE)
 * </pre>
 *
 * <p>To list all branches.</p>
 *
 * <pre>
 * def branches = grgit.branch.list(mode: BranchListOp.Mode.ALL)
 * </pre>
 *
 * <p>To list all branches contains specified commit</p>
 *
 * <pre>
 * def branches = grgit.branch.list(contains: %Commit hash or tag name%)
 * </pre>
 *
 * See <a href="http://git-scm.com/docs/git-branch">git-branch Manual Page</a>.
 *
 * @since 0.2.0
 * @see <a href="http://git-scm.com/docs/git-branch">git-branch Manual Page</a>
 */
class BranchListOp implements Callable<List<Branch>> {
    private final Repository repo

    /**
     * Which branches to return.
     */
    Mode mode = Mode.LOCAL

    /**
     * Commit ref branches must contains
     */
    Object contains = null

    BranchListOp(Repository repo) {
        this.repo = repo
    }

    List<Branch> call() {
        ListBranchCommand cmd = repo.jgit.branchList()
        cmd.listMode = mode.jgit
        if (contains) {
            cmd.contains = new ResolveService(repo).toRevisionString(contains)
        }
        try {
            return cmd.call().collect {
                JGitUtil.resolveBranch(repo, it.name)
            }
        } catch (GitAPIException e) {
            throw new GrgitException('Problem listing branches.', e)
        }
    }

    static enum Mode {
        ALL(ListBranchCommand.ListMode.ALL),
        REMOTE(ListBranchCommand.ListMode.REMOTE),
        LOCAL(null)

        private final ListBranchCommand.ListMode jgit

        private Mode(ListBranchCommand.ListMode jgit) {
            this.jgit = jgit
        }
    }
}
