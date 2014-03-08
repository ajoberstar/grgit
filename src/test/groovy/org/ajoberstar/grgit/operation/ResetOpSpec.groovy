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

import spock.lang.Specification

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec
import org.ajoberstar.grgit.service.RepositoryService
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.api.Git

import org.junit.Rule
import org.junit.rules.TemporaryFolder

class ResetOpSpec extends SimpleGitOpSpec {
	List commits = []

	def setup() {
		repoFile('1.bat') << '1'
		repoFile('something/2.txt') << '2'
		repoFile('test/3.bat') << '3'
		repoFile('test/4.txt') << '4'
		repoFile('test/other/5.txt') << '5'
		grgit.add(patterns:['.'])
		commits << grgit.commit(message: 'Test')
		repoFile('1.bat') << '2'
		repoFile('test/3.bat') << '4'
		grgit.add(patterns:['.'])
		commits << grgit.commit(message: 'Test')
		repoFile('1.bat') << '3'
		repoFile('something/2.txt') << '2'
		grgit.add(patterns:['.'])
		repoFile('test/other/5.txt') << '6'
		repoFile('test/4.txt') << '5'
	}

	def 'reset soft changes HEAD only'() {
		when:
		grgit.reset(mode:ResetOp.Mode.SOFT, commit:commits[0].id)
		then:
		commits[0] == grgit.head()
		grgit.status() == new Status(
			[] as Set,
			['1.bat', 'test/3.bat', 'something/2.txt'] as Set,
			[] as Set,
			[] as Set,
			['test/4.txt', 'test/other/5.txt'] as Set,
			[] as Set
		)
	}

	def 'reset mixed changes HEAD and index'() {
		when:
		grgit.reset(mode:ResetOp.Mode.MIXED, commit:commits[0].id)
		then:
		commits[0] == grgit.head()
		grgit.status() == new Status(
			[] as Set,
			[] as Set,
			[] as Set,
			[] as Set,
			['1.bat', 'test/3.bat', 'test/4.txt', 'something/2.txt', 'test/other/5.txt'] as Set,
			[] as Set
		)
	}

	def 'reset hard changes HEAD, index, and working tree'() {
		when:
		grgit.reset(mode:ResetOp.Mode.HARD, commit:commits[0].id)
		then:
		commits[0] == grgit.head()
		grgit.status() == new Status(
			[] as Set,
			[] as Set,
			[] as Set,
			[] as Set,
			[] as Set,
			[] as Set
		)
	}

	def 'reset merge not supported by JGit'() {
		when:
		grgit.reset(mode:ResetOp.Mode.MERGE, commit:commits[0].id)
		then:
		thrown(UnsupportedOperationException)
	}

	def 'reset keep not supported by JGit'() {
		when:
		grgit.reset(mode:ResetOp.Mode.KEEP, commit:commits[0].id)
		then:
		thrown(UnsupportedOperationException)
	}

	def 'reset with paths changes index only'() {
		when:
		grgit.reset(paths:['something/2.txt'])
		then:
		commits[1] == grgit.head()
		grgit.status() == new Status(
			[] as Set,
			['1.bat'] as Set,
			[] as Set,
			[] as Set,
			['test/4.txt', 'something/2.txt', 'test/other/5.txt'] as Set,
			[] as Set
		)
	}

	def 'reset with paths and mode set not supported'() {
		when:
		grgit.reset(mode:ResetOp.Mode.HARD, paths:['.'])
		then:
		thrown(IllegalStateException)
	}

	// def 'reset merge changes HEAD, index, and working tree but not unstaged changes'() {}

	// def 'reset merge aborts when unstaged changes to file that differs between commit and index'() {}

	// def 'reset keep changes HEAD, index, and working tree if no unstaged changes'() {}

	// def 'reset keep aborts if any unstaged changes different from commit'() {}
}
