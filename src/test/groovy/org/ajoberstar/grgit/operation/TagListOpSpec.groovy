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

import org.ajoberstar.grgit.Tag
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec

class TagListOpSpec extends SimpleGitOpSpec {
	List commits = []

	def setup() {
		repoFile('1.txt') << '1'
		commits << grgit.commit(message: 'do', all: true)
		grgit.tag.add(name: 'tag1', message: 'My message')

		repoFile('1.txt') << '2'
		commits << grgit.commit(message: 'do', all: true)
		grgit.tag.add(name: 'tag2', message: 'My other\nmessage')
	}

	def 'tag list lists all tags'() {
		expect:
		grgit.tag.list() == [
			new Tag(commits[0], person, 'refs/tags/tag1', 'My message', 'My message'),
			new Tag(commits[1], person, 'refs/tags/tag2', 'My other\nmessage', 'My other message')
		]
	}
}
