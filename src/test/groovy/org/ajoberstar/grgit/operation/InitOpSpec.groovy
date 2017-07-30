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

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.fixtures.GitTestUtil

import org.junit.Rule
import org.junit.rules.TemporaryFolder

import spock.lang.Specification

class InitOpSpec extends Specification {
  @Rule TemporaryFolder tempDir = new TemporaryFolder()

  File repoDir

  def setup() {
    repoDir = tempDir.newFolder('repo')
  }

  def 'init with bare true does not have a working tree'() {
    when:
    def grgit = Grgit.init(dir: repoDir, bare: true)
    then:
    !GitTestUtil.repoFile(grgit, '.', false).listFiles().collect { it.name }.contains('.git')
  }

  def 'init with bare false has a working tree'() {
    when:
    def grgit = Grgit.init(dir: repoDir, bare: false)
    then:
    GitTestUtil.repoFile(grgit, '.', false).listFiles().collect { it.name } == ['.git']
  }

  def 'init repo can be deleted after being closed'() {
    given:
    def grgit = Grgit.init(dir: repoDir, bare: false)
    when:
    grgit.close()
    then:
    repoDir.deleteDir()
  }
}
