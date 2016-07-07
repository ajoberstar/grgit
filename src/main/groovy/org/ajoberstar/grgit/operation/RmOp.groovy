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

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.exception.GrgitException

import org.eclipse.jgit.api.RmCommand
import org.eclipse.jgit.api.errors.GitAPIException

/**
 * Remove files from the index and (optionally) delete them from the working tree.
 * Note that wildcards are not supported.
 *
 * <p>Remove specific file or directory from both the index and working tree.</p>
 *
 * <pre>
 * grgit.remove(patterns: ['1.txt', 'some/dir'])
 * grgit.remove(patterns: ['1.txt', 'some/dir'], cached: false)
 * </pre>
 *
 * <p>Remove specific file or directory from the index, but leave the in the working tree.</p>
 *
 * <pre>
 * grgit.remove(patterns: ['1.txt', 'some/dir'], cached: true)
 * </pre>
 *
 * See <a href="http://git-scm.com/docs/git-rm">git-rm Manual Page</a>.
 *
 * @since 0.1.0
 * @see <a href="http://git-scm.com/docs/git-rm">git-rm Manual Page</a>
 */
class RmOp implements Callable<Void> {
	private final Repository repo

	/**
	 * The file patterns to remove.
	 */
	Set<String> patterns = []

	/**
	 * {@code true} if files should only be removed from the index,
	 * {@code false} (the default) otherwise.
	 */
	boolean cached = false

	RmOp(Repository repo) {
		this.repo = repo
	}

	Void call() {
		RmCommand cmd = repo.jgit.rm()
		patterns.each { cmd.addFilepattern(it.replaceAll('\\\\', '/')) }
		cmd.cached = cached
		try {
			cmd.call()
			return null
		} catch (GitAPIException e) {
			throw new GrgitException('Problem removing files from index.', e)
		}
	}
}
