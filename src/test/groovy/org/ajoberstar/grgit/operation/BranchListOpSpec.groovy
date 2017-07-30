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

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.fixtures.GitTestUtil
import org.ajoberstar.grgit.fixtures.MultiGitOpSpec
import spock.lang.Unroll

class BranchListOpSpec extends MultiGitOpSpec {
  Grgit localGrgit
  Grgit remoteGrgit

  def setup() {
    remoteGrgit = init('remote')

    repoFile(remoteGrgit, '1.txt') << '1'
    remoteGrgit.commit(message: 'do', all: true)

    remoteGrgit.branch.add(name: 'my-branch')

    repoFile(remoteGrgit, '2.txt') << '2'
    remoteGrgit.commit(message: 'another', all: true)
    remoteGrgit.tag.add(name: 'test-tag');

    localGrgit = clone('local', remoteGrgit)
  }

  @Unroll('list branch with #arguments lists #expected')
  def 'list branch without arguments only lists local'() {
    given:
    def expectedBranches = expected.collect { GitTestUtil.branch(*it) }
    def head = localGrgit.head()
    expect:
    localGrgit.branch.list(arguments) == expectedBranches
    where:
    arguments											  | expected
    [:]													| [['refs/heads/master', 'refs/remotes/origin/master']]
    [mode: BranchListOp.Mode.LOCAL]						| [['refs/heads/master', 'refs/remotes/origin/master']]
    [mode: BranchListOp.Mode.REMOTE]					   | [['refs/remotes/origin/master'], ['refs/remotes/origin/my-branch']]
    [mode: BranchListOp.Mode.ALL]						  | [['refs/heads/master', 'refs/remotes/origin/master'], ['refs/remotes/origin/master'], ['refs/remotes/origin/my-branch']]
    [mode: BranchListOp.Mode.REMOTE, contains: 'test-tag'] | [['refs/remotes/origin/master']]
  }

  def 'list branch receives Commit object as contains flag'() {
    given:
    def expectedBranches = [GitTestUtil.branch('refs/remotes/origin/master')]
    def head = localGrgit.head()
    def arguments = [mode: BranchListOp.Mode.REMOTE, contains: head]
    expect:
    localGrgit.branch.list(arguments) == expectedBranches
  }
}
