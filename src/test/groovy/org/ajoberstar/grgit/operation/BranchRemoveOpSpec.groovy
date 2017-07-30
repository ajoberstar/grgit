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

import org.ajoberstar.grgit.Branch
import org.ajoberstar.grgit.fixtures.GitTestUtil
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec
import org.eclipse.jgit.api.errors.GitAPIException

import spock.lang.Unroll

class BranchRemoveOpSpec extends SimpleGitOpSpec {
  def setup() {
    repoFile('1.txt') << '1'
    grgit.commit(message: 'do', all: true)

    grgit.branch.add(name: 'branch1')

    repoFile('1.txt') << '2'
    grgit.commit(message: 'do', all: true)

    grgit.branch.add(name: 'branch2')

    grgit.checkout(branch: 'branch3', createBranch: true)
    repoFile('1.txt') << '3'
    grgit.commit(message: 'do', all: true)

    grgit.checkout(branch: 'master')
  }

  def 'branch remove with empty list does nothing'() {
    expect:
    grgit.branch.remove() == []
    grgit.branch.list() == branches('branch1', 'branch2', 'branch3', 'master')
  }

  def 'branch remove with one branch removes branch'() {
    expect:
    grgit.branch.remove(names: ['branch2']) == ['refs/heads/branch2']
    grgit.branch.list() == branches('branch1', 'branch3', 'master')
  }

  def 'branch remove with multiple branches remvoes branches'() {
    expect:
    grgit.branch.remove(names: ['branch2', 'branch1']) == ['refs/heads/branch2', 'refs/heads/branch1']
    grgit.branch.list() == branches('branch3', 'master')
  }

  def 'branch remove with invalid branches skips invalid and removes others'() {
    expect:
    grgit.branch.remove(names: ['branch2', 'blah4']) == ['refs/heads/branch2']
    grgit.branch.list() == branches('branch1', 'branch3', 'master')
  }

  def 'branch remove with unmerged branch and force false fails'() {
    when:
    grgit.branch.remove(names: ['branch3'])
    then:
    thrown(GitAPIException)
  }

  def 'branch remove with unmerged branch and force true works'() {
    expect:
    grgit.branch.remove(names: ['branch3'], force: true) == ['refs/heads/branch3']
    grgit.branch.list() == branches('branch1', 'branch2', 'master')
  }

  @Unroll('branch remove with current branch and force #force fails')
  def 'branch remove with current branch fails, even with force'() {
    when:
    grgit.branch.remove(names: ['master'], force: force)
    then:
    thrown(GitAPIException)
    where:
    force << [true, false]
  }

  private List<Branch> branches(String... branches) {
    return branches.collect { GitTestUtil.branch("refs/heads/${it}") }
  }
}
