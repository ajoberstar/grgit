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

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Person
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.api.CommitCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.revwalk.RevCommit

/**
 * Commits staged changes to the repository. Returns the new {@code Commit}.
 *
 * <p>To commit all staged changes.</p>
 *
 * <pre>
 * def commit = grgit.commit(message: 'something about the change')
 * def commit = grgit.commit(message: 'something about the change', all: false)
 * </pre>
 *
 * <p>To commit changes to all previously tracked files.</p>
 *
 * <pre>
 * def commit = grgit.commit(message: 'something about the change', all: true)
 * </pre>
 *
 * <p>To amend the previous commit.</p>
 *
 * <pre>
 * def commit = grgit.commit(message: 'something about the change', amend: true)
 * </pre>
 *
 * <p>To commit changes authored by another person.</p>
 *
 * <pre>
 * def commit = grgit.commit(message: 'something about the change', auther: new Person('Bruce Wayne', 'bruce.wayne@wayneindustries.com'))
 * </pre>
 *
 * See <a href="http://git-scm.com/docs/git-commit">git-commit Manual Reference.</a>
 *
 * @since 0.1.0
 * @see <a href="http://git-scm.com/docs/git-commit">git-commit Manual Reference.</a>
 */
class CommitOp implements Callable<Commit> {
    private final Repository repo

    /**
     * Commit message.
     */
    String message

    /**
     * Comment to put in the reflog.
     */
    String reflogComment

    /**
     * The person who committed the changes. Uses the git-config
     * setting, if {@code null}.
     */
    Person committer

    /**
     * The person who authored the changes. Uses the git-config
     * setting, if {@code null}.
     */
    Person author

    /**
     * Only include these paths when committing. {@code null} to
     * include all staged changes.
     */
    Set<String> paths = []

    /**
     * Commit changes to all previously tracked files, even if
     * they aren't staged, if {@code true}.
     */
    boolean all = false

    /**
     * {@code true} if the previous commit should be amended with
     * these changes.
     */
    boolean amend = false

    CommitOp(Repository repo) {
        this.repo = repo
    }

    Commit call() {
        CommitCommand cmd = repo.jgit.commit()
        cmd.message = message
        cmd.reflogComment = reflogComment
        if (committer) { cmd.committer = new PersonIdent(committer.name, committer.email) }
        if (author) { cmd.author = new PersonIdent(author.name, author.email) }
        paths.each { cmd.setOnly(it) }
        if (all) { cmd.all = all }
        cmd.amend = amend
        try {
            RevCommit commit = cmd.call()
            return JGitUtil.convertCommit(commit)
        } catch (GitAPIException e) {
            throw new GrgitException('Problem committing changes.', e)
        }
    }
}
