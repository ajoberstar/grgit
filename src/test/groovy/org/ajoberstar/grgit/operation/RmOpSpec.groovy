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

class RmOpSpec extends SimpleGitOpSpec {
  def setup() {
    repoFile('1.bat') << '1'
    repoFile('something/2.txt') << '2'
    repoFile('test/3.bat') << '3'
    repoFile('test/4.txt') << '4'
    repoFile('test/other/5.txt') << '5'
    grgit.add(patterns:['.'])
    grgit.commit(message: 'Test')
  }

  def 'removing specific file only removes that file'() {
    given:
    def paths = ['1.bat'] as Set
    when:
    grgit.remove(patterns:['1.bat'])
    then:
    grgit.status() == new Status(staged: [removed: paths])
    paths.every { !repoFile(it).exists() }
  }

  def 'removing specific directory removes all files within it'() {
    given:
    def paths = ['test/3.bat', 'test/4.txt', 'test/other/5.txt'] as Set
    when:
    grgit.remove(patterns:['test'])
    then:
    grgit.status() == new Status(staged: [removed: paths])
    paths.every { !repoFile(it).exists() }
  }

  def 'removing file pattern does not work due to lack of JGit support'() {
    given:
    def paths = ['1.bat', 'something/2.txt', 'test/3.bat', 'test/4.txt', 'test/other/5.txt'] as Set
    when:
    grgit.remove(patterns:['**/*.txt'])
    then:
    grgit.status().clean
    /*
     * TODO: get it to work like this
     * status.removed == ['something/2.txt', 'test/4.txt', 'test/other/5.txt'] as Set
     */
     paths.every { repoFile(it).exists() }
  }

  def 'removing with cached true only removes files from index'() {
    given:
    def paths = ['something/2.txt'] as Set
    when:
    grgit.remove(patterns:['something'], cached:true)
    then:
    grgit.status() == new Status(staged: [removed: paths], unstaged: [added: paths])
    paths.every { repoFile(it).exists() }
  }
}
