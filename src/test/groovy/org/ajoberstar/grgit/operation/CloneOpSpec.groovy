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

class CloneOpSpec extends MultiGitOpSpec {
  File repoDir

  Grgit remoteGrgit
  String remoteUri

  def remoteBranchesFilter = { it =~ $/^refs/remotes/origin/$ }
  def localBranchesFilter = { it =~ $/^refs/heads/$ }
  def lastName = { it.split('/')[-1] }

  def setup() {
    // TODO: Convert branching and tagging to Grgit.
    repoDir = tempDir.newFolder('local')

    remoteGrgit = init('remote')
    remoteUri = remoteGrgit.repository.rootDir.toURI()

    repoFile(remoteGrgit, '1.txt') << '1'
    remoteGrgit.commit(message: 'do', all: true)

    remoteGrgit.branch.add(name: 'branch1')

    repoFile(remoteGrgit, '1.txt') << '2'
    remoteGrgit.commit(message: 'do', all: true)

    remoteGrgit.tag.add(name: 'tag1')

    repoFile(remoteGrgit, '1.txt') << '3'
    remoteGrgit.commit(message: 'do', all: true)

    remoteGrgit.branch.add(name: 'branch2')
  }

  def 'clone with non-existent uri fails'() {
    when:
    Grgit.clone(dir: repoDir, uri: 'file:///bad/uri')
    then:
    thrown(GitAPIException)
  }

  def 'clone with default settings clones as expected'() {
    when:
    def grgit = Grgit.clone(dir: repoDir, uri: remoteUri)
    then:
    grgit.head() == remoteGrgit.head()
    GitTestUtil.branches(grgit).findAll(remoteBranchesFilter).collect(lastName) == GitTestUtil.branches(remoteGrgit).collect(lastName)
    GitTestUtil.branches(grgit).findAll(localBranchesFilter).collect(lastName) == ['master']
    GitTestUtil.tags(grgit).collect(lastName) == ['tag1']
    GitTestUtil.remotes(grgit) == ['origin']
  }

  def 'clone with different remote does not use origin'() {
    when:
    def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, remote: 'oranges')
    then:
    GitTestUtil.remotes(grgit) == ['oranges']
  }

  def 'clone with bare true does not have a working tree'() {
    when:
    def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, bare: true)
    then:
    !repoFile(grgit, '.', false).listFiles().collect { it.name }.contains('.git')
  }

  def 'clone with checkout false does not check out a working tree'() {
    when:
    def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, checkout: false)
    then:
    repoFile(grgit, '.', false).listFiles().collect { it.name } == ['.git']
  }

  def 'clone with checkout false and refToCheckout set fails'() {
    when:
    def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, checkout: false, refToCheckout: 'branch2')
    then:
    thrown(IllegalArgumentException)
  }

  def 'clone with refToCheckout set to simple branch name works'() {
    when:
    def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, refToCheckout: 'branch1')
    then:
    grgit.head() == remoteGrgit.resolve.toCommit('branch1')
    GitTestUtil.branches(grgit).findAll(remoteBranchesFilter).collect(lastName) == GitTestUtil.branches(remoteGrgit).collect(lastName)
    GitTestUtil.branches(grgit).findAll(localBranchesFilter).collect(lastName) == ['branch1']
    GitTestUtil.tags(grgit).collect(lastName) == ['tag1']
    GitTestUtil.remotes(grgit) == ['origin']
  }

  def 'clone with refToCheckout set to simple tag name works'() {
    when:
    def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, refToCheckout: 'tag1')
    then:
    grgit.head() == remoteGrgit.resolve.toCommit('tag1')
    GitTestUtil.branches(grgit).findAll(remoteBranchesFilter).collect(lastName) == GitTestUtil.branches(remoteGrgit).collect(lastName)
    GitTestUtil.branches(grgit).findAll(localBranchesFilter).collect(lastName) == []
    GitTestUtil.tags(grgit).collect(lastName) == ['tag1']
    GitTestUtil.remotes(grgit) == ['origin']
  }

  def 'clone with refToCheckout set to full ref name works'() {
    when:
    def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, refToCheckout: 'refs/heads/branch2')
    then:
    grgit.head() == remoteGrgit.resolve.toCommit('branch2')
    GitTestUtil.branches(grgit).findAll(remoteBranchesFilter).collect(lastName) == GitTestUtil.branches(remoteGrgit).collect(lastName)
    GitTestUtil.branches(grgit).findAll(localBranchesFilter).collect(lastName) == ['branch2']
    GitTestUtil.tags(grgit).collect(lastName) == ['tag1']
    GitTestUtil.remotes(grgit) == ['origin']
  }

  def 'cloned repo can be deleted'() {
    given:
    def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, refToCheckout: 'refs/heads/branch2')
    when:
    grgit.close()
    then:
    repoDir.deleteDir()
  }
}
