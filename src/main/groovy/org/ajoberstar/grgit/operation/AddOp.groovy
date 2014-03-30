/*
 * Copyright 2012-2014 the original author or authors.
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

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.exception.GrgitException

import org.eclipse.jgit.api.AddCommand
import org.eclipse.jgit.api.errors.GitAPIException

/**
 * Adds files to the index.
 *
 * <p>
 *   To add specific files or directories to the path. Wildcards are not
 *   supported.
 * </p>
 *
 * <pre>
 * grgit.add(patterns: ['1.txt', 'some/dir'])
 * </pre>
 *
 * <p>To add changes to all currently tracked files.</p>
 *
 * <pre>
 * grgit.add(update: true)
 * </pre>
 *
 * See <a href="http://git-scm.com/docs/git-add">git-add Manual Page</a>.
 *
 * @since 0.1.0
 * @see <a href="http://git-scm.com/docs/git-add">git-add Manual Page</a>
 */
class AddOp implements Callable<Void> {
	private final Repository repo

	/**
	 * Patterns of files to add to the index.
	 */
	Set<String> patterns = []

	/**
	 * {@code true} if changes to all currently tracked files should be added
	 * to the index, {@code false} otherwise.
	 */
	boolean update = false

	AddOp(Repository repo) {
		this.repo = repo
	}

	Void call() {
		AddCommand cmd = repo.jgit.add()
		patterns.each { cmd.addFilepattern(it) }
		cmd.update = update
		try {
			cmd.call()
			return null
		} catch (GitAPIException e) {
			throw new GrgitException('Problem adding changes to index.', e)
		}
	}
}
