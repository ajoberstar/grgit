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

class BranchListOpSpec extends MultiGitOpSpec {
	RepositoryService localGrgit
	RepositoryService remoteGrgit

	def setup() {
		remoteGrgit = init('remote')

		repoFile(remoteGrgit, '1.txt') << '1'
		remoteGrgit.commit(message: 'do', all: true)

		remoteGrgit.branch.add(name: 'my-branch')

		localGrgit = clone('local', remoteGrgit)
	}

	@Unroll('list branch with #arguments lists #expected')
	def 'list branch without arguments only lists local'() {
		given:
		def expectedBranches = expected.collect { GitTestUtil.branch(*it) }
		expect:
		localGrgit.branch.list(arguments) == expectedBranches
		where:
		arguments                        | expected
		[:]                              | [['refs/heads/master', 'refs/remotes/origin/master']]
		[mode: BranchListOp.Mode.LOCAL]  | [['refs/heads/master', 'refs/remotes/origin/master']]
		[mode: BranchListOp.Mode.REMOTE] | [['refs/remotes/origin/master'], ['refs/remotes/origin/my-branch']]
		[mode: BranchListOp.Mode.ALL]    | [['refs/heads/master', 'refs/remotes/origin/master'], ['refs/remotes/origin/master'], ['refs/remotes/origin/my-branch']]
	}
}
