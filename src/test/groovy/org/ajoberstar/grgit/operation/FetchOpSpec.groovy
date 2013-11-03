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

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Person
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.exception.GrGitException
import org.ajoberstar.grgit.service.RepositoryService
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode

import org.junit.Rule
import org.junit.rules.TemporaryFolder

class FetchOpSpec extends Specification {
	@Rule TemporaryFolder tempDir = new TemporaryFolder()

	RepositoryService localGrgit
	RepositoryService remoteGrgit

	def setup() {
		File remoteRepoDir = tempDir.newFolder('remote')
		Git.init().setDirectory(remoteRepoDir).call()
		remoteGrgit = Grgit.open(remoteRepoDir)

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
		localGrgit = Grgit.open(localRepoDir)

		repoFile(remoteGrgit, '1.txt') << '2'
		remoteGrgit.commit(message: 'do', all: true)

		remoteGrgit.repository.git.tag().with {
			name = 'reachable-tag'
			delegate.call()
		}

		remoteGrgit.repository.git.branchCreate().with {
			name = 'sub/mine1'
			delegate.call()
		}

		remoteGrgit.repository.git.checkout().with {
			name = 'unreachable-branch'
			createBranch = true
			delegate.call()
		}

		repoFile(remoteGrgit, '1.txt') << '2.5'
		remoteGrgit.commit(message: 'do-unreachable', all: true)

		remoteGrgit.repository.git.tag().with {
			name = 'unreachable-tag'
			delegate.call()
		}

		remoteGrgit.repository.git.checkout().with {
			name = 'master'
			delegate.call()
		}

		repoFile(remoteGrgit, '1.txt') << '3'
		remoteGrgit.commit(message: 'do', all: true)

		remoteGrgit.repository.git.branchCreate().with {
			name = 'sub/mine2'
			delegate.call()
		}

		remoteGrgit.repository.git.branchDelete().with {
			setBranchNames('my-branch', 'unreachable-branch')
			force = true
			delegate.call()
		}
	}

	def 'fetch from non-existent remote fails'() {
		when:
		localGrgit.fetch(remote: 'fake')
		then:
		thrown(GrGitException)
	}

	def 'fetch without other settings, brings down correct commits'() {
		given:
		def remoteHead = remoteGrgit.log(maxCommits: 1).find()
		def localHead = { -> JGitUtil.resolveCommit(localGrgit.repository, 'refs/remotes/origin/master') }
		assert localHead() != remoteHead
		when:
		localGrgit.fetch()
		then:
		localHead() == remoteHead
	}

	def 'fetch with prune true, removes refs deleted in the remote'() {
		given:
		def branches = { grgit -> grgit.repository.git.branchList().call().collect { it.name.split('/')[-1] } }
		def remoteBranches = { grgit ->
			grgit.repository.git.branchList().with {
				listMode = ListMode.REMOTE
				delegate.call()
			}.collect { it.name.split('/')[-1] }
		}
		assert remoteBranches(localGrgit) - branches(remoteGrgit)
		when:
		localGrgit.fetch(prune: true)
		then:
		remoteBranches(localGrgit) == branches(remoteGrgit)
	}

	@Unroll('fetch with tag mode #mode fetches #expectedTags')
	def 'fetch with different tag modes behave as expected'() {
		given:
		def tags = { grgit -> grgit.repository.git.tagList().call().collect { it.name.split('/')[-1] } }
		assert !tags(localGrgit)
		when:
		localGrgit.fetch(tagMode: mode)
		then:
		assert tags(localGrgit) == expectedTags
		where:
		mode  | expectedTags
		TagMode.NONE | []
		TagMode.AUTO | ['reachable-tag']
		TagMode.ALL  | ['reachable-tag', 'unreachable-tag']
	}

	def 'fetch with refspecs fetches those branches'() {
		given:
		def branches = { grgit ->
			grgit.repository.git.branchList().with {
				listMode = ListMode.ALL
				delegate.call()
			}.collect { it.name }
		}
		assert branches(localGrgit) == [
			'refs/heads/master',
			'refs/remotes/origin/master',
			'refs/remotes/origin/my-branch']
		when:
		localGrgit.fetch(refSpecs: ['+refs/heads/sub/*:refs/remotes/origin/banana/*'])
		then:
		branches(localGrgit) == [
			'refs/heads/master',
			'refs/remotes/origin/banana/mine1',
			'refs/remotes/origin/banana/mine2',
			'refs/remotes/origin/master',
			'refs/remotes/origin/my-branch']
	}

	private File repoFile(RepositoryService grgit, String path, boolean makeDirs = true) {
		def file = new File(grgit.repository.rootDir, path)
		if (makeDirs) file.parentFile.mkdirs()
		return file
	}
}
