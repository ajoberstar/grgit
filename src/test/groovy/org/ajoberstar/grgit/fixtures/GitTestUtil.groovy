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
package org.ajoberstar.grgit.fixtures

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.service.RepositoryService
import org.ajoberstar.grgit.util.JGitUtil
import org.eclipse.jgit.api.ListBranchCommand.ListMode
import org.eclipse.jgit.transport.RemoteConfig

final class GitTestUtil {
	private GitTestUtil() {
		throw new AssertionError('Cannot instantiate this class.')
	}

	static File repoFile(RepositoryService grgit, String path, boolean makeDirs = true) {
		def file = new File(grgit.repository.rootDir, path)
		if (makeDirs) file.parentFile.mkdirs()
		return file
	}

	static List branches(RepositoryService grgit, boolean trim = true) {
		return grgit.repository.git.branchList().with {
			listMode = ListMode.ALL
			delegate.call()
		}.collect { trim ? it.name - 'refs/heads/' : it.name }
	}

	static List remoteBranches(RepositoryService grgit) {
		return grgit.repository.git.branchList().with {
			listMode = ListMode.REMOTE
			delegate.call()
		}.collect { it.name - 'refs/remotes/origin/' }
	}

	static List tags(RepositoryService grgit) {
		return grgit.repository.git.tagList().call().collect {
			it.name - 'refs/tags/'
		}
	}

	static List remotes(RepositoryService grgit) {
		def jgitConfig = grgit.repository.git.repo.config
		return RemoteConfig.getAllRemoteConfigs(jgitConfig).collect { it.name}
	}

	static Commit resolve(RepositoryService grgit, String revstr) {
		return JGitUtil.resolveCommit(grgit.repository, revstr)
	}

	static void configure(RepositoryService grgit, Closure closure) {
		def config = grgit.repository.git.repo.config
		config.with(closure)
		config.save()
	}
}
