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

import spock.lang.Unroll

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Person
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.fixtures.GitTestUtil
import org.ajoberstar.grgit.fixtures.MultiGitOpSpec
import org.ajoberstar.grgit.service.RepositoryService
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode

import org.junit.Rule
import org.junit.rules.TemporaryFolder

class FetchOpSpec extends MultiGitOpSpec {
	RepositoryService localGrgit
	RepositoryService remoteGrgit

	def setup() {
		// TODO: convert after branch and tag available
		remoteGrgit = init('remote')

		repoFile(remoteGrgit, '1.txt') << '1'
		remoteGrgit.commit(message: 'do', all: true)

		remoteGrgit.repository.git.branchCreate().with {
			name = 'my-branch'
			delegate.call()
		}

		localGrgit = clone('local', remoteGrgit)

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

		remoteGrgit.checkout {
			branch = 'unreachable-branch'
			createBranch = true
		}

		repoFile(remoteGrgit, '1.txt') << '2.5'
		remoteGrgit.commit(message: 'do-unreachable', all: true)

		remoteGrgit.repository.git.tag().with {
			name = 'unreachable-tag'
			delegate.call()
		}

		remoteGrgit.checkout(branch: 'master')

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
		thrown(GrgitException)
	}

	def 'fetch without other settings, brings down correct commits'() {
		given:
		def remoteHead = remoteGrgit.log(maxCommits: 1).find()
		def localHead = { -> GitTestUtil.resolve(localGrgit, 'refs/remotes/origin/master') }
		assert localHead() != remoteHead
		when:
		localGrgit.fetch()
		then:
		localHead() == remoteHead
	}

	def 'fetch with prune true, removes refs deleted in the remote'() {
		given:
		assert GitTestUtil.remoteBranches(localGrgit) - GitTestUtil.branches(remoteGrgit)
		when:
		localGrgit.fetch(prune: true)
		then:
		GitTestUtil.remoteBranches(localGrgit) == GitTestUtil.branches(remoteGrgit)
	}

	@Unroll('fetch with tag mode #mode fetches #expectedTags')
	def 'fetch with different tag modes behave as expected'() {
		given:
		assert !GitTestUtil.tags(localGrgit)
		when:
		localGrgit.fetch(tagMode: mode)
		then:
		assert GitTestUtil.tags(localGrgit) == expectedTags
		where:
		mode  | expectedTags
		TagMode.NONE | []
		TagMode.AUTO | ['reachable-tag']
		TagMode.ALL  | ['reachable-tag', 'unreachable-tag']
	}

	def 'fetch with refspecs fetches those branches'() {
		given:
		assert GitTestUtil.branches(localGrgit, false) == [
			'refs/heads/master',
			'refs/remotes/origin/master',
			'refs/remotes/origin/my-branch']
		when:
		localGrgit.fetch(refSpecs: ['+refs/heads/sub/*:refs/remotes/origin/banana/*'])
		then:
		GitTestUtil.branches(localGrgit, false) == [
			'refs/heads/master',
			'refs/remotes/origin/banana/mine1',
			'refs/remotes/origin/banana/mine2',
			'refs/remotes/origin/master',
			'refs/remotes/origin/my-branch']
	}
}
