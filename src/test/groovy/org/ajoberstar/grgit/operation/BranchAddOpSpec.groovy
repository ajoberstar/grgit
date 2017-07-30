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
import org.ajoberstar.grgit.fixtures.MultiGitOpSpec
import org.eclipse.jgit.api.errors.GitAPIException

import spock.lang.Unroll

class BranchAddOpSpec extends MultiGitOpSpec {
  Grgit localGrgit
  Grgit remoteGrgit

  List commits = []

  def setup() {
    remoteGrgit = init('remote')

    repoFile(remoteGrgit, '1.txt') << '1'
    commits << remoteGrgit.commit(message: 'do', all: true)

    repoFile(remoteGrgit, '1.txt') << '2'
    commits << remoteGrgit.commit(message: 'do', all: true)

    remoteGrgit.branch.add(name: 'my-branch')

    localGrgit = clone('local', remoteGrgit)
  }

  def 'branch add with name creates branch pointing to current HEAD'() {
    when:
    localGrgit.branch.add(name: 'test-branch')
    then:
    localGrgit.branch.list() == [GitTestUtil.branch('refs/heads/master', 'refs/remotes/origin/master'), GitTestUtil.branch('refs/heads/test-branch')]
    localGrgit.resolve.toCommit('test-branch') == localGrgit.head()
  }

  def 'branch add with name and startPoint creates branch pointing to startPoint'() {
    when:
    localGrgit.branch.add(name: 'test-branch', startPoint: commits[0].id)
    then:
    localGrgit.branch.list() == [GitTestUtil.branch('refs/heads/master', 'refs/remotes/origin/master'), GitTestUtil.branch('refs/heads/test-branch')]
    localGrgit.resolve.toCommit('test-branch') == commits[0]
  }

  def 'branch add fails to overwrite existing branch'() {
    given:
    localGrgit.branch.add(name: 'test-branch', startPoint: commits[0].id)
    when:
    localGrgit.branch.add(name: 'test-branch')
    then:
    thrown(GitAPIException)
  }

  def 'branch add with mode set but no start point fails'() {
    when:
    localGrgit.branch.add(name: 'my-branch', mode: mode)
    then:
    thrown(IllegalStateException)
    where:
    mode << BranchAddOp.Mode.values()
  }

  @Unroll('branch add with #mode mode starting at #startPoint tracks #trackingBranch')
  def 'branch add with mode and start point behaves correctly'() {
    given:
    localGrgit.branch.add(name: 'test-branch', startPoint: commits[0].id)
    expect:
    localGrgit.branch.add(name: 'local-branch', startPoint: startPoint, mode: mode) == GitTestUtil.branch('refs/heads/local-branch', trackingBranch)
    localGrgit.resolve.toCommit('local-branch') == localGrgit.resolve.toCommit(startPoint)
    where:
    mode					  | startPoint		 | trackingBranch
    null					  | 'origin/my-branch' | 'refs/remotes/origin/my-branch'
    BranchAddOp.Mode.TRACK	| 'origin/my-branch' | 'refs/remotes/origin/my-branch'
    BranchAddOp.Mode.NO_TRACK | 'origin/my-branch' | null
    null					  | 'test-branch'	  | null
    BranchAddOp.Mode.TRACK	| 'test-branch'	  | 'refs/heads/test-branch'
    BranchAddOp.Mode.NO_TRACK | 'test-branch'	  | null
  }

  @Unroll('branch add with no name, #mode mode, and a start point fails')
  def 'branch add with no name fails'() {
    when:
    localGrgit.branch.add(startPoint: 'origin/my-branch', mode: mode)
    then:
    thrown(GitAPIException)
    where:
    mode << [null, BranchAddOp.Mode.TRACK, BranchAddOp.Mode.NO_TRACK]
  }
}
