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

import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec

class DescribeOpSpec extends SimpleGitOpSpec {
  def setup() {
    grgit.commit(message:"initial commit")
    grgit.tag.add(name:"initial")
    grgit.commit(message:"another commit")
    grgit.tag.add(name:"another")
  }

  def 'with tag'() {
    expect:
    grgit.describe() == "another"
  }

  def 'with additional commit'(){
    when:
    repoFile('1.txt') << '1'
    grgit.add(patterns:['1.txt'])
    grgit.commit(message:  "another commit")
    then:
    grgit.describe().startsWith("another-1-")
  }

  def 'from differnt commit'(){
    when:
    repoFile('1.txt') << '1'
    grgit.add(patterns:['1.txt'])
    grgit.commit(message:  "another commit")
    then:
    grgit.describe(commit: 'HEAD~2') == "initial"
  }

  def 'with long description'() {
    expect:
    grgit.describe(longDescr: true).startsWith("another-0-")
  }

  def 'with match'() {
    expect:
    grgit.describe(match: ['initial*']).startsWith("initial-1-")
  }
}
