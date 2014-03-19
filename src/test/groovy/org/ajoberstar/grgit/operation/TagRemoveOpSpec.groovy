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

import org.ajoberstar.grgit.Tag
import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Person
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec
import org.ajoberstar.grgit.service.RepositoryService
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.api.Git

import org.junit.Rule
import org.junit.rules.TemporaryFolder

class TagRemoveOpSpec extends SimpleGitOpSpec {
	def setup() {
		repoFile('1.txt') << '1'
		grgit.commit(message: 'do', all: true)
		grgit.tag.add(name: 'tag1')

		repoFile('1.txt') << '2'
		grgit.commit(message: 'do', all: true)
		grgit.tag.add(name: 'tag2', annotate: false)
	}

	def 'tag remove with empty list does nothing'() {
		expect:
		grgit.tag.remove() == []
		grgit.tag.list().collect { it.name } == ['refs/tags/tag1', 'refs/tags/tag2']
	}

	def 'tag remove with one tag removes tag'() {
		expect:
		grgit.tag.remove(names: ['tag2']) == ['refs/tags/tag2']
		grgit.tag.list().collect { it.name } == ['refs/tags/tag1']
	}

	def 'tag remove with multiple tags removes tags'() {
		expect:
		grgit.tag.remove(names: ['tag2', 'tag1']) as Set == ['refs/tags/tag2', 'refs/tags/tag1'] as Set
		grgit.tag.list() == []
	}

	def 'tag remove with invalid tags skips invalid and removes others'() {
		expect:
		grgit.tag.remove(names: ['tag2', 'blah4']) == ['refs/tags/tag2']
		grgit.tag.list().collect { it.name } == ['refs/tags/tag1']
	}
}
