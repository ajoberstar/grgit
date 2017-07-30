/*
 * Copyright 2012-2017 the original author or authors.
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
  List tags = []

  def setup() {
    repoFile('1.txt') << '1'
    commits << grgit.commit(message: 'do', all: true)
    tags << grgit.tag.add(name: 'tag1', message: 'My message')

    repoFile('1.txt') << '2'
    commits << grgit.commit(message: 'do', all: true)
    tags << grgit.tag.add(name: 'tag2', message: 'My other\nmessage')

    tags << grgit.tag.add(name: 'tag3', message: 'My next message.', pointsTo: 'tag1')
  }

  def 'tag list lists all tags'() {
    expect:
    grgit.tag.list() == tags
  }
}
