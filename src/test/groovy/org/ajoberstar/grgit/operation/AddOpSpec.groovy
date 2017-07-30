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

class AddOpSpec extends SimpleGitOpSpec {
  def 'adding specific file only adds that file'() {
    given:
    repoFile('1.txt') << '1'
    repoFile('2.txt') << '2'
    repoFile('test/3.txt') << '3'
    when:
    grgit.add(patterns:['1.txt'])
    then:
    grgit.status() == new Status(
      staged: [added: ['1.txt']],
      unstaged: [added: ['2.txt', 'test/3.txt']]
    )
  }

  def 'adding specific directory adds all files within it'() {
    given:
    repoFile('1.txt') << '1'
    repoFile('something/2.txt') << '2'
    repoFile('test/3.txt') << '3'
    repoFile('test/4.txt') << '4'
    repoFile('test/other/5.txt') << '5'
    when:
    grgit.add(patterns:['test'])
    then:
    grgit.status() == new Status(
      staged: [added: ['test/3.txt', 'test/4.txt', 'test/other/5.txt']],
      unstaged: [added: ['1.txt', 'something/2.txt']]
    )
  }

  def 'adding file pattern does not work due to lack of JGit support'() {
    given:
    repoFile('1.bat') << '1'
    repoFile('something/2.txt') << '2'
    repoFile('test/3.bat') << '3'
    repoFile('test/4.txt') << '4'
    repoFile('test/other/5.txt') << '5'
    when:
    grgit.add(patterns:['**/*.txt'])
    then:
    grgit.status() == new Status(
      unstaged: [added: ['1.bat', 'test/3.bat', 'something/2.txt', 'test/4.txt', 'test/other/5.txt']]
    )
    /*
     * TODO: get it to work like this
     * status.added == ['something/2.txt', 'test/4.txt', 'test/other/5.txt'] as Set
     * status.untracked == ['1.bat', 'test/3.bat'] as Set
     */
  }

  def 'adding with update true only adds/removes files already in the index'() {
    given:
    repoFile('1.bat') << '1'
    repoFile('something/2.txt') << '2'
    repoFile('test/3.bat') << '3'
    grgit.add(patterns:['.'])
    grgit.repository.jgit.commit().setMessage('Test').call()
    repoFile('1.bat') << '1'
    repoFile('something/2.txt') << '2'
    assert repoFile('test/3.bat').delete()
    repoFile('test/4.txt') << '4'
    repoFile('test/other/5.txt') << '5'
    when:
    grgit.add(patterns:['.'], update:true)
    then:
    grgit.status() == new Status(
      staged: [modified: ['1.bat', 'something/2.txt'], removed: ['test/3.bat']],
      unstaged: [added: ['test/4.txt', 'test/other/5.txt']]
    )
  }
}
