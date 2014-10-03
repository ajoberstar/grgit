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

import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec

class CheckoutOpSpec extends SimpleGitOpSpec {
	List commits = []

	def setup() {
		repoFile('1.txt') << '1'
		commits << grgit.commit(message: 'do', all: true)

		repoFile('1.txt') << '2'
		commits << grgit.commit(message: 'do', all: true)

		grgit.branch.add(name: 'my-branch')

		repoFile('1.txt') << '3'
		commits << grgit.commit(message: 'do', all: true)
	}

	def 'checkout with existing branch and createBranch false works'() {
		when:
		grgit.checkout(branch: 'my-branch')
		then:
		grgit.head() == grgit.resolveCommit('my-branch')
		grgit.branch.current.fullName == 'refs/heads/my-branch'
	}

	def 'checkout with existing branch, createBranch true fails'() {
		when:
		grgit.checkout(branch: 'my-branch', createBranch: true)
		then:
		thrown(GrgitException)
	}

	def 'checkout with non-existent branch and createBranch false fails'() {
		when:
		grgit.checkout(branch: 'fake')
		then:
		thrown(GrgitException)
	}

	def 'checkout with non-existent branch and createBranch true works'() {
		when:
		grgit.checkout(branch: 'new-branch', createBranch: true)
		then:
		grgit.branch.current.fullName == 'refs/heads/new-branch'
		grgit.head() == grgit.resolveCommit('master')
	}

	def 'checkout with non-existent branch, createBranch true, and startPoint works'() {
		when:
		grgit.checkout(branch: 'new-branch', createBranch: true, startPoint: 'my-branch')
		then:
		grgit.branch.current.fullName == 'refs/heads/new-branch'
		grgit.head() == grgit.resolveCommit('my-branch')
	}

	def 'checkout with no branch name and createBranch true fails'() {
		when:
		grgit.checkout(createBranch: true)
		then:
		thrown(IllegalArgumentException)
	}

	def 'checkout with existing branch and orphan true fails'() {
		when:
		grgit.checkout(branch: 'my-branch', orphan: true)
		then:
		thrown(GrgitException)
	}

	def 'checkout with non-existent branch and orphan true works'() {
		when:
		grgit.checkout(branch: 'orphan-branch', orphan: true)
		then:
		grgit.branch.current.fullName == 'refs/heads/orphan-branch'
	}

	def 'checkout with non-existent branch, orphan true, and startPoint works'() {
		when:
		grgit.checkout(branch: 'orphan-branch', orphan: true, startPoint: 'my-branch')
		then:
		grgit.branch.current.fullName == 'refs/heads/orphan-branch'
	}

	def 'checkout with no branch name and orphan true fails'() {
		when:
		grgit.checkout(orphan: true)
		then:
		thrown(IllegalArgumentException)
	}
}
