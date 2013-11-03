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
import spock.lang.Unroll

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Person
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.exception.GrGitException
import org.ajoberstar.grgit.service.RepositoryService
import org.ajoberstar.grgit.service.ServiceFactory
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode

import org.junit.Rule
import org.junit.rules.TemporaryFolder

class PushOpSpec extends Specification {
	@Rule TemporaryFolder tempDir = new TemporaryFolder()

	RepositoryService localGrgit
	RepositoryService remoteGrgit

	def setup() {
		File remoteRepoDir = tempDir.newFolder('remote')
		Git.init().setDirectory(remoteRepoDir).call()
		remoteGrgit = createService(remoteRepoDir)

		repoFile(remoteGrgit, '1.txt') << '1'
		remoteGrgit.commit(message: 'do', all: true)

		remoteGrgit.repository.git.branchCreate().with {
			name = 'my-branch'
			delegate.call()
		}

		File localRepoDir = tempDir.newFolder('local')
		Git.cloneRepository().with {
			directory = localRepoDir
			uri = remoteRepoDir.toURI()
			cloneAllBranches = true
			delegate.call()
		}
		localGrgit = createService(localRepoDir)

		localGrgit.repository.git.checkout().with {
			name = 'my-branch'
			createBranch = true
			delegate.call()
		}

		repoFile(localGrgit, '1.txt') << '1.5'
		localGrgit.commit(message: 'do', all: true)

		localGrgit.repository.git.tag().with {
			name = 'tag1'
			delegate.call()
		}

		localGrgit.repository.git.checkout().with {
			name = 'master'
			delegate.call()
		}

		repoFile(localGrgit, '1.txt') << '2'
		localGrgit.commit(message: 'do', all: true)

		localGrgit.repository.git.tag().with {
			name = 'tag2'
			delegate.call()
		}
	}

	private RepositoryService createService(File dir) {
		Repository repo = ServiceFactory.createRepository(dir)
		return ServiceFactory.createService(repo)
	}

	def 'push to non-existent remote fails'() {
		when:
		localGrgit.push(remote: 'fake')
		then:
		thrown(GrGitException)
	}

	def 'push without other settings pushes correct commits'() {
		when:
		localGrgit.push()
		then:
		head(localGrgit, 'refs/heads/master') == head(remoteGrgit, 'refs/heads/master')
		head(localGrgit, 'refs/heads/my-branch') != head(remoteGrgit, 'refs/heads/my-branch')
		!tags(remoteGrgit)
	}

	def 'push with all true pushes all branches'() {
		when:
		localGrgit.push(all: true)
		then:
		head(localGrgit, 'refs/heads/master') == head(remoteGrgit, 'refs/heads/master')
		head(localGrgit, 'refs/heads/my-branch') == head(remoteGrgit, 'refs/heads/my-branch')
		!tags(remoteGrgit)
	}

	def 'push with tags true pushes all tags'() {
		when:
		localGrgit.push(tags: true)
		then:
		head(localGrgit, 'refs/heads/master') != head(remoteGrgit, 'refs/heads/master')
		head(localGrgit, 'refs/heads/my-branch') != head(remoteGrgit, 'refs/heads/my-branch')
		tags(localGrgit) == tags(remoteGrgit)
	}

	def 'push with refs only pushes those refs'() {
		when:
		localGrgit.push(refsOrSpecs: ['my-branch'])
		then:
		head(localGrgit, 'refs/heads/master') != head(remoteGrgit, 'refs/heads/master')
		head(localGrgit, 'refs/heads/my-branch') == head(remoteGrgit, 'refs/heads/my-branch')
		!tags(remoteGrgit)
	}

	def 'push with refSpecs only pushes those refs'() {
		when:
		localGrgit.push(refsOrSpecs: ['+refs/heads/my-branch:refs/heads/other-branch'])
		then:
		head(localGrgit, 'refs/heads/master') != head(remoteGrgit, 'refs/heads/master')
		head(localGrgit, 'refs/heads/my-branch') != head(remoteGrgit, 'refs/heads/my-branch')
		head(localGrgit, 'refs/heads/my-branch') == head(remoteGrgit, 'refs/heads/other-branch')
		!tags(remoteGrgit)
	}

	private File repoFile(RepositoryService grgit, String path, boolean makeDirs = true) {
		def file = new File(grgit.repository.rootDir, path)
		if (makeDirs) file.parentFile.mkdirs()
		return file
	}

	private Commit head(RepositoryService grgit, String ref) {
		return JGitUtil.resolveCommit(grgit.repository, ref)
	}

	private List tags(RepositoryService grgit) {
		return grgit.repository.git.tagList().call().collect { it.name }
	}
}
