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
    repoDir = new File(tempDir, 'local')

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

  def 'clone with all true works'() {
    when:
    def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, all: true)
    then:
    grgit.head() == remoteGrgit.resolve.toCommit('master')
    GitTestUtil.branches(grgit).findAll(remoteBranchesFilter).collect(lastName) == GitTestUtil.branches(remoteGrgit).collect(lastName)
    GitTestUtil.branches(grgit).findAll(localBranchesFilter).collect(lastName) == ['master']
  }

  def 'clone with all false has the same effect as all true'() {
    when:
    def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, all: false)
    then:
    grgit.head() == remoteGrgit.resolve.toCommit('master')
    GitTestUtil.branches(grgit).findAll(remoteBranchesFilter).collect(lastName) == GitTestUtil.branches(remoteGrgit).collect(lastName)
    GitTestUtil.branches(grgit).findAll(localBranchesFilter).collect(lastName) == ['master']
  }

  def 'clone with all false and explicitly set branches works'() {
    when:
    def branches = ['refs/heads/master', 'refs/heads/branch1']
    def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, all: false, branches: branches)
    then:
    grgit.head() == remoteGrgit.resolve.toCommit('master')
    GitTestUtil.branches(grgit).findAll(remoteBranchesFilter).collect(lastName).sort() == ['master', 'branch1'].sort()
    GitTestUtil.branches(grgit).findAll(localBranchesFilter).collect(lastName) == ['master']
  }

  def 'clone with all false and 1 depth'() {
    when:
    def grgit = Grgit.clone(dir: repoDir, uri: remoteUri, all: false, depth: 1)
    then:
    grgit.head().id == remoteGrgit.resolve.toCommit('master').id
    grgit.head().parentIds.isEmpty()
    GitTestUtil.branches(grgit).findAll(localBranchesFilter).collect(lastName) == ['master']
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
