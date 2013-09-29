/*
 * Copyright 2012-2013 the original author or authors.
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

import spock.lang.Specification

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.service.RepositoryService
import org.ajoberstar.grgit.service.ServiceFactory

import org.eclipse.jgit.api.Git

import org.junit.Rule
import org.junit.rules.TemporaryFolder

class ApplyOpSpec extends Specification {
	@Rule TemporaryFolder tempDir = new TemporaryFolder()

	RepositoryService grgit

	def setup() {
		File repoDir = tempDir.newFolder('repo')
		Git git = Git.init().setDirectory(repoDir).call()
		Repository repo = ServiceFactory.createRepository(repoDir)
		grgit = ServiceFactory.createService(repo)
	}

	def 'apply with no patch fails'() {
		when:
		grgit.stage.apply()
		then:
		thrown(IllegalStateException)
	}

	def 'apply with patch succeeds'() {
		given:
		repoFile('1.txt') << 'something'
		repoFile('2.txt') << 'something else\n'
		grgit.stage.add(patterns:['.'])
		grgit.repository.git.commit().setMessage('Test').call()
		def patch = tempDir.newFile()
		this.class.getResourceAsStream('/org/ajoberstar/grgit/operation/sample.patch').withStream { stream ->
			patch << stream
		}
		when:
		grgit.stage.apply(patch: patch)
		then:
		repoFile('1.txt').text == 'something'
		repoFile('2.txt').text == 'something else\nis being added\n'
		repoFile('3.txt').text == 'some new stuff\n'
	}

	private File repoFile(String path, boolean makeDirs = true) {
		def file = new File(grgit.repository.rootDir, path)
		if (makeDirs) file.parentFile.mkdirs()
		return file
	}
}
