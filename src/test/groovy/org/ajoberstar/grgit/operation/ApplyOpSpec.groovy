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

class ApplyOpSpec extends SimpleGitOpSpec {
  def 'apply with no patch fails'() {
    when:
    grgit.apply()
    then:
    thrown(IllegalStateException)
  }

  def 'apply with patch succeeds'() {
    given:
    repoFile('1.txt') << 'something'
    repoFile('2.txt') << 'something else\n'
    grgit.add(patterns:['.'])
    grgit.commit(message: 'Test')
    def patch = tempDir.newFile()
    this.class.getResourceAsStream('/org/ajoberstar/grgit/operation/sample.patch').withStream { stream ->
      patch << stream
    }
    when:
    grgit.apply(patch: patch)
    then:
    repoFile('1.txt').text == 'something'
    repoFile('2.txt').text == 'something else\nis being added\n'
    repoFile('3.txt').text == 'some new stuff\n'
  }
}
