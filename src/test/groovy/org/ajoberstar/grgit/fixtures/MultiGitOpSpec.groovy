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

import spock.lang.Specification

import org.ajoberstar.grgit.service.RepositoryService
import org.ajoberstar.grgit.Grgit

import org.eclipse.jgit.api.Git

import org.junit.Rule
import org.junit.rules.TemporaryFolder

class MultiGitOpSpec extends Specification {
	@Rule TemporaryFolder tempDir = new TemporaryFolder()

	protected RepositoryService init(String name) {
		File repoDir = tempDir.newFolder(name)
		Git git = Git.init().setDirectory(repoDir).call()
		return Grgit.open(repoDir)
	}

	protected RepositoryService clone(String name, RepositoryService remote) {
		File repoDir = tempDir.newFolder(name)
		return Grgit.clone {
			dir = repoDir
			uri = remote.repository.rootDir.toURI()
		}
	}

	protected File repoFile(RepositoryService grgit, String path, boolean makeDirs = true) {
		return GitTestUtil.repoFile(grgit, path, makeDirs)
	}
}
