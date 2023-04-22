package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.fixtures.GitTestUtil
import org.ajoberstar.grgit.fixtures.MultiGitOpSpec
import org.eclipse.jgit.api.errors.GitAPIException
import spock.lang.Unroll

class FetchOpSpec extends MultiGitOpSpec {
  Grgit localGrgit
  Grgit remoteGrgit

  def setup() {
    // TODO: convert after branch and tag available
    remoteGrgit = init('remote')

    repoFile(remoteGrgit, '1.txt') << '1'
    remoteGrgit.commit(message: 'do', all: true)

    remoteGrgit.branch.add(name: 'my-branch')

    localGrgit = clone('local', remoteGrgit)

    repoFile(remoteGrgit, '1.txt') << '2'
    remoteGrgit.commit(message: 'do', all: true)

    remoteGrgit.tag.add(name: 'reachable-tag')
    remoteGrgit.branch.add(name: 'sub/mine1')

    remoteGrgit.checkout {
      branch = 'unreachable-branch'
      createBranch = true
    }

    repoFile(remoteGrgit, '1.txt') << '2.5'
    remoteGrgit.commit(message: 'do-unreachable', all: true)

    remoteGrgit.tag.add(name: 'unreachable-tag')

    remoteGrgit.checkout(branch: 'master')

    repoFile(remoteGrgit, '1.txt') << '3'
    remoteGrgit.commit(message: 'do', all: true)

    remoteGrgit.branch.add(name: 'sub/mine2')
    remoteGrgit.branch.remove(names: ['my-branch', 'unreachable-branch'], force: true)
  }

  def 'fetch from non-existent remote fails'() {
    when:
    localGrgit.fetch(remote: 'fake')
    then:
    thrown(GitAPIException)
  }

  def 'fetch without other settings, brings down correct commits'() {
    given:
    def remoteHead = remoteGrgit.log(maxCommits: 1).find()
    def localHead = { -> GitTestUtil.resolve(localGrgit, 'refs/remotes/origin/master') }
    assert localHead() != remoteHead
    when:
    localGrgit.fetch()
    then:
    localHead() == remoteHead
  }

  def 'fetch with prune true, removes refs deleted in the remote'() {
    given:
    assert GitTestUtil.remoteBranches(localGrgit) - GitTestUtil.branches(remoteGrgit, true)
    when:
    localGrgit.fetch(prune: true)
    then:
    GitTestUtil.remoteBranches(localGrgit) == GitTestUtil.branches(remoteGrgit, true)
  }

  @Unroll('fetch with tag mode #mode fetches #expectedTags')
  def 'fetch with different tag modes behave as expected'() {
    given:
    assert !GitTestUtil.tags(localGrgit)
    when:
    localGrgit.fetch(tagMode: mode)
    then:
    assert GitTestUtil.tags(localGrgit) == expectedTags
    where:
    mode         | expectedTags
    'none' | []
    'auto' | ['reachable-tag']
    'all'  | ['reachable-tag', 'unreachable-tag']
  }

  def 'fetch with refspecs fetches those branches'() {
    given:
    assert GitTestUtil.branches(localGrgit) == [
      'refs/heads/master',
      'refs/remotes/origin/master',
      'refs/remotes/origin/my-branch']
    when:
    localGrgit.fetch(refSpecs: ['+refs/heads/sub/*:refs/remotes/origin/banana/*'])
    then:
    GitTestUtil.branches(localGrgit) == [
      'refs/heads/master',
      'refs/remotes/origin/banana/mine1',
      'refs/remotes/origin/banana/mine2',
      'refs/remotes/origin/master',
      'refs/remotes/origin/my-branch']
  }

  def 'fetch with depth does a shallow fetch'() {
    given:
    def shallowGrgit = init('shallow')
    shallowGrgit.remote.add(name: 'origin', url: remoteGrgit.repository.rootDir.toURI())
    when:
    shallowGrgit.fetch(remote: 'origin', depth: 1)
    shallowGrgit.checkout(branch: 'master', createBranch: true, startPoint: 'origin/master')
    then:
    shallowGrgit.head().id == remoteGrgit.resolve.toCommit('master').id
    shallowGrgit.head().parentIds.isEmpty()
  }
}
