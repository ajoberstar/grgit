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

import spock.lang.Unroll

import org.ajoberstar.grgit.Branch
import org.ajoberstar.grgit.BranchStatus
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

class BranchStatusOpSpec extends MultiGitOpSpec {
	RepositoryService localGrgit
	RepositoryService remoteGrgit

	def setup() {
		remoteGrgit = init('remote')

		repoFile(remoteGrgit, '1.txt') << '1'
		remoteGrgit.commit(message: 'do', all: true)

		remoteGrgit.checkout(branch: 'up-to-date', createBranch: true)

		repoFile(remoteGrgit, '1.txt') << '2'
		remoteGrgit.commit(message: 'do', all: true)

		remoteGrgit.checkout(branch: 'master')
		remoteGrgit.checkout(branch: 'out-of-date', createBranch: true)

		localGrgit = clone('local', remoteGrgit)

		localGrgit.branch.add(name: 'up-to-date', startPoint: 'origin/up-to-date')
		localGrgit.branch.add(name: 'out-of-date', startPoint: 'origin/out-of-date')
		localGrgit.checkout(branch: 'out-of-date')

		repoFile(remoteGrgit, '1.txt') << '3'
		remoteGrgit.commit(message: 'do', all: true)

		repoFile(localGrgit, '1.txt') << '4'
		localGrgit.commit(message: 'do', all: true)
		repoFile(localGrgit, '1.txt') << '5'
		localGrgit.commit(message: 'do', all: true)

		localGrgit.branch.add(name: 'no-track')

		localGrgit.fetch()
	}

	def 'branch status on branch that is not tracking fails'() {
		when:
		localGrgit.branch.status(branch: 'no-track')
		then:
		thrown(GrgitException)
	}

	@Unroll('branch status on #branch gives correct counts')
	def 'branch status on branch that is tracking gives correct counts'() {
		expect:
		localGrgit.branch.status(branch: branch) == status
		where:
		branch        | status
		'up-to-date'  | new BranchStatus(branch: GitTestUtil.branch('refs/heads/up-to-date', 'refs/remotes/origin/up-to-date'), aheadCount: 0, behindCount: 0)
		'out-of-date' | new BranchStatus(branch: GitTestUtil.branch('refs/heads/out-of-date', 'refs/remotes/origin/out-of-date'), aheadCount: 2, behindCount: 1)
	}
}
