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
import org.ajoberstar.grgit.Person

import org.eclipse.jgit.api.Git

import org.junit.Rule
import org.junit.rules.TemporaryFolder

class SimpleGitOpSpec extends Specification {
	@Rule TemporaryFolder tempDir = new TemporaryFolder()

	RepositoryService grgit
	Person person = new Person('Bruce Wayne', 'bruce.wayne@wayneindustries.com')

	def setup() {
		File repoDir = tempDir.newFolder('repo')
		Git git = Git.init().setDirectory(repoDir).call()
		git.repo.config.with {
			setString('user', null, 'name', person.name)
			setString('user', null, 'email', person.email)
			save()
		}
		grgit = Grgit.open(repoDir)
	}

	protected File repoFile(String path, boolean makeDirs = true) {
		return GitTestUtil.repoFile(grgit, path, makeDirs)
	}

	protected File repoDir(String path) {
		return GitTestUtil.repoDir(grgit, path)
	}
}
