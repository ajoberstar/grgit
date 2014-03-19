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
import org.ajoberstar.grgit.fixtures.MultiGitOpSpec
import org.ajoberstar.grgit.service.RepositoryService
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode

import org.junit.Rule
import org.junit.rules.TemporaryFolder

class BranchAddOpSpec extends MultiGitOpSpec {
	RepositoryService localGrgit
	RepositoryService remoteGrgit

	List commits = []

	def setup() {
		remoteGrgit = init('remote')

		repoFile(remoteGrgit, '1.txt') << '1'
		commits << remoteGrgit.commit(message: 'do', all: true)

		repoFile(remoteGrgit, '1.txt') << '2'
		commits << remoteGrgit.commit(message: 'do', all: true)

		remoteGrgit.branch.add(name: 'my-branch')

		localGrgit = clone('local', remoteGrgit)
	}

	def 'branch add with name creates branch pointing to current HEAD'() {
		when:
		localGrgit.branch.add(name: 'test-branch')
		then:
		localGrgit.branch.list() == [new Branch('refs/heads/master'), new Branch('refs/heads/test-branch')]
		localGrgit.resolveCommit('test-branch') == localGrgit.head()
	}

	def 'branch add with name and startPoint creates branch pointing to startPoint'() {
		when:
		localGrgit.branch.add(name: 'test-branch', startPoint: commits[0].id)
		then:
		localGrgit.branch.list() == [new Branch('refs/heads/master'), new Branch('refs/heads/test-branch')]
		localGrgit.resolveCommit('test-branch') == commits[0]
	}

	def 'branch add without force fails to overwrite existing branch'() {
		given:
		localGrgit.branch.add(name: 'test-branch', startPoint: commits[0].id)
		when:
		localGrgit.branch.add(name: 'test-branch')
		then:
		thrown(GrgitException)
	}

	def 'branch add with force overwrites existing branch'() {
		given:
		localGrgit.branch.add(name: 'test-branch', startPoint: commits[0].id)
		when:
		localGrgit.branch.add(name: 'test-branch', force: true)
		then:
		localGrgit.resolveCommit('test-branch') == localGrgit.head()
	}
}
