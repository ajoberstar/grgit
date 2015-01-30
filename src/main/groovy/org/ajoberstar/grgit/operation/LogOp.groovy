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
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.api.LogCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.ObjectId

/**
 * Gets a log of commits in the repository. Returns a list of {@link Commit}s.
 * Since a Git history is not necessarilly a line, these commits may not be in
 * a strict order.
 *
 * <p>Get a full log of commits from the current HEAD and back.</p>
 *
 * <pre>
 * def history = grgit.log()
 * </pre>
 *
 * <p>Get log of commits between two points.</p>
 *
 * <pre>
 * def history = grgit.log {
 *  range 'v1.0', 'v2.0'
 * }
 * </pre>
 *
 * <p>Get a list of the most recent 5 commits.</p>
 *
 * <pre>
 * def history = grgit.log(maxCommits: 5)
 * </pre>
 *
 * <p>Get a log of commits skipping the most recent 3.</p>
 *
 * <pre>
 * def history = grgit.log(skipCommits: 3)
 * </pre>
 *
 * See <a href="http://git-scm.com/docs/git-log">git-log Manual Page</a>.
 *
 * @since 0.1.0
 * @see <a href="http://git-scm.com/docs/git-log">git-log Manual Page</a>
 */
class LogOp implements Callable<List<Commit>> {
	private final Repository repo

	List includes = []
	List excludes = []
	List paths = []
	int skipCommits = -1
	int maxCommits = -1

	LogOp(Repository repo) {
		this.repo = repo
	}

	void range(Object since, Object until) {
		excludes << since
		includes << until
	}

	List<Commit> call() {
		LogCommand cmd = repo.jgit.log()
		includes.each { include ->
			ObjectId object = JGitUtil.resolveObject(repo, include)
			cmd.add(object)
		}
		excludes.each { exclude ->
			ObjectId object = JGitUtil.resolveObject(repo, exclude)
			cmd.not(object)
		}
		paths.each { path ->
			cmd.addPath(path)
		}
		cmd.skip = skipCommits
		cmd.maxCount = maxCommits
		try {
			return cmd.call().collect { JGitUtil.convertCommit(it) }.asImmutable()
		} catch (GitAPIException e) {
			throw new GrgitException('Problem retrieving log.', e)
		}
	}
}
