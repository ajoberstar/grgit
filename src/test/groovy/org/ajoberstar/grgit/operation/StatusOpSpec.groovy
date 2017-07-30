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

import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec
import org.eclipse.jgit.api.errors.GitAPIException

class StatusOpSpec extends SimpleGitOpSpec {
  def setup() {
    4.times { repoFile("${it}.txt") << "1" }
    grgit.add(patterns: ['.'])
    grgit.commit(message: 'Test')
    grgit.checkout(branch: 'conflict', createBranch: true)
    repoFile('1.txt') << '2'
    grgit.add(patterns: ['.'])
    grgit.commit(message: 'conflicting change')
    grgit.checkout(branch: 'master')
    repoFile('1.txt') << '3'
    grgit.add(patterns: ['.'])
    grgit.commit(message: 'other change')
  }

  def 'with no changes all methods return empty list'() {
    expect:
    grgit.status() == new Status()
  }

  def 'new unstaged file detected'() {
    given:
    repoFile('5.txt') << '5'
    repoFile('6.txt') << '6'
    expect:
    grgit.status() == new Status(unstaged: [added: ['5.txt', '6.txt']])
  }

  def 'unstaged modified files detected'() {
    given:
    repoFile('2.txt') << '2'
    repoFile('3.txt') << '3'
    expect:
    grgit.status() == new Status(unstaged: [modified: ['2.txt', '3.txt']])
  }

  def 'unstaged deleted files detected'() {
    given:
    assert repoFile('1.txt').delete()
    assert repoFile('2.txt').delete()
    expect:
    grgit.status() == new Status(unstaged: [removed: ['1.txt', '2.txt']])
  }

  def 'staged new files detected'() {
    given:
    repoFile('5.txt') << '5'
    repoFile('6.txt') << '6'
    when:
    grgit.add(patterns: ['.'])
    then:
    grgit.status() == new Status(staged: [added: ['5.txt', '6.txt']])
  }

  def 'staged modified files detected'() {
    given:
    repoFile('1.txt') << '5'
    repoFile('2.txt') << '6'
    when:
    grgit.add(patterns: ['.'])
    then:
    grgit.status() == new Status(staged: [modified: ['1.txt', '2.txt']])
  }

  def 'staged removed files detected'() {
    given:
    assert repoFile('3.txt').delete()
    assert repoFile('0.txt').delete()
    when:
    grgit.add(patterns: ['.'], update: true)
    then:
    grgit.status() == new Status(staged: [removed: ['3.txt', '0.txt']])
  }

  def 'conflict files detected'() {
    when:
    grgit.merge(head: 'conflict')
    then:
    grgit.status() == new Status(conflicts: ['1.txt'])
    thrown(IllegalStateException)
  }
}
