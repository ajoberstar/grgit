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

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.util.CoercionUtil

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.InitCommand
import org.eclipse.jgit.api.errors.GitAPIException

/**
 * Initializes a new repository. Returns a {@link Grgit} pointing
 * to the resulting repository.
 *
 * <p>To initialize a new repository with a working tree.</p>
 *
 * <pre>
 * def grgit = Grgit.init(dir: 'gradle-git')
 * def grgit = Grgit.init(dir: 'gradle-git', bare: false)
 * </pre>
 *
 * <p>To initialize a new repository without a working tree.</p>
 *
 * <pre>
 * def grgit = Grgit.init(dir: 'gradle-git', bare: true)
 * </pre>
 *
 * See <a href="http://git-scm.com/docs/git-init">git-init Manual Reference.</a>
 *
 * @since 0.1.0
 * @see <a href="http://git-scm.com/docs/git-init">git-init Manual Reference.</a>
 */
class InitOp implements Callable<Grgit> {
    /**
     * {@code true} if the repository should not have a
     * working tree, {@code false} (the default) otherwise
     */
    boolean bare = false

    /**
     * The directory to initialize the repository in.
     * @see {@link CoercionUtil#toFile(Object)}
     */
    Object dir

    Grgit call() {
        InitCommand cmd = Git.init()
        cmd.bare = bare
        cmd.directory = CoercionUtil.toFile(dir)
        try {
            Git jgit = cmd.call()
            Repository repo = new Repository(CoercionUtil.toFile(dir), jgit, null)
            return new Grgit(repo)
        } catch (GitAPIException e) {
            throw new GrgitException('Problem initializing repository.', e)
        }
    }
}
