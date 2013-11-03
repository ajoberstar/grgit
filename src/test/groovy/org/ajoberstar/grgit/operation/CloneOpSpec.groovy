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

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.service.RepositoryService

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode
import org.eclipse.jgit.transport.RemoteConfig

import org.junit.Rule
import org.junit.rules.TemporaryFolder

class CloneOpSpec extends Specification {
	@Rule TemporaryFolder tempDir = new TemporaryFolder()

	File repoDir

	RepositoryService remoteGrgit
	String remoteUri

	def remoteBranchesFilter = { it =~ $/^refs/remotes/origin/$}
	def localBranchesFilter = { it =~ $/^refs/heads/$}
	def lastName = { it.split('/')[-1] }

	def setup() {
		repoDir = tempDir.newFolder('repo')

		File remoteRepoDir = tempDir.newFolder('remote')
		remoteGrgit = Grgit.init(dir: remoteRepoDir)
		remoteUri = remoteRepoDir.toURI()

		repoFile(remoteGrgit, '1.txt') << '1'
		remoteGrgit.commit(message: 'do', all: true)

		remoteGrgit.repository.git.branchCreate().with {
			name = 'branch1'
			delegate.call()
		}

		repoFile(remoteGrgit, '1.txt') << '2'
		remoteGrgit.commit(message: 'do', all: true)

		remoteGrgit.repository.git.tag().with {
			name = 'tag1'
			delegate.call()
		}

		repoFile(remoteGrgit, '1.txt') << '3'
		remoteGrgit.commit(message: 'do', all: true)

		remoteGrgit.repository.git.branchCreate().with {
			name = 'branch2'
			delegate.call()
		}
	}

	def 'clone with non-existent uri fails'() {
		when:
		Grgit.clone(dir: repoDir, uri: 'file:///bad/uri')
		then:
		thrown(GrgitException)
	}

	def 'clone with default settings clones as expected'() {
		when:
		def grgit = Grgit.clone(dir: repoDir, uri: remoteUri)
		then:
		grgit.head() == remoteGrgit.head()
		branches(grgit).findAll(remoteBranchesFilter).collect(lastName) == branches(remoteGrgit).collect(lastName)
		branches(grgit).findAll(localBranchesFilter).collect(lastName) == ['master']
		tags(grgit).collect(lastName) == ['tag1']
		remotes(grgit) == ['origin']
	}

	def 'clone with different remote does not use origin'() {
		when:
		def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, remote: 'oranges')
		then:
		remotes(grgit) == ['oranges']
	}

	def 'clone with bare true does not have a working tree'() {
		when:
		def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, bare: true)
		then:
		!repoFile(grgit, '.', false).listFiles().collect { it.name }.contains('.git')
	}

	def 'clone with checkout false does not check out a working tree'() {
		when:
		def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, checkout: false)
		then:
		repoFile(grgit, '.', false).listFiles().collect { it.name } == ['.git']
	}

	def 'clone with checkout false and refToCheckout set fails'() {
		when:
		def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, checkout: false, refToCheckout: 'branch2')
		then:
		thrown(IllegalArgumentException)
	}

	def 'clone with refToCheckout set to simple branch name works'() {
		when:
		def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, refToCheckout: 'branch1')
		then:
		grgit.head() == remoteGrgit.resolveCommit('branch1')
		branches(grgit).findAll(remoteBranchesFilter).collect(lastName) == branches(remoteGrgit).collect(lastName)
		branches(grgit).findAll(localBranchesFilter).collect(lastName) == ['branch1']
		tags(grgit).collect(lastName) == ['tag1']
		remotes(grgit) == ['origin']
	}

	def 'clone with refToCheckout set to simple tag name works'() {
		when:
		def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, refToCheckout: 'tag1')
		then:
		grgit.head() == remoteGrgit.resolveCommit('tag1')
		branches(grgit).findAll(remoteBranchesFilter).collect(lastName) == branches(remoteGrgit).collect(lastName)
		branches(grgit).findAll(localBranchesFilter).collect(lastName) == []
		tags(grgit).collect(lastName) == ['tag1']
		remotes(grgit) == ['origin']
	}

	def 'clone with refToCheckout set to full ref name works'() {
		when:
		def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, refToCheckout: 'refs/heads/branch2')
		then:
		grgit.head() == remoteGrgit.resolveCommit('branch2')
		branches(grgit).findAll(remoteBranchesFilter).collect(lastName) == branches(remoteGrgit).collect(lastName)
		branches(grgit).findAll(localBranchesFilter).collect(lastName) == ['branch2']
		tags(grgit).collect(lastName) == ['tag1']
		remotes(grgit) == ['origin']
	}

	private File repoFile(RepositoryService grgit, String path, boolean makeDirs = true) {
		def file = new File(grgit.repository.rootDir, path)
		if (makeDirs) file.parentFile.mkdirs()
		return file
	}

	private List branches(RepositoryService grgit) {
		return grgit.repository.git.branchList().with {
			listMode = ListMode.ALL
			delegate.call()
		}.collect { it.name }
	}

	private List tags(RepositoryService grgit) {
		return grgit.repository.git.tagList().call().collect { it.name }
	}

	private List remotes(RepositoryService grgit) {
		def jgitConfig = grgit.repository.git.repo.config
		return RemoteConfig.getAllRemoteConfigs(jgitConfig).collect { it.name}
	}
}
