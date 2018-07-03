package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.PushException
import org.ajoberstar.grgit.fixtures.GitTestUtil
import org.ajoberstar.grgit.fixtures.MultiGitOpSpec
import org.eclipse.jgit.api.errors.GitAPIException

class PushOpSpec extends MultiGitOpSpec {
  Grgit localGrgit
  Grgit remoteGrgit

  def setup() {
    // TODO: Conver to Grgit after branch and tag

    remoteGrgit = init('remote')

    repoFile(remoteGrgit, '1.txt') << '1'
    remoteGrgit.commit(message: 'do', all: true)

    remoteGrgit.branch.add(name: 'my-branch')

    remoteGrgit.checkout(branch: 'some-branch', createBranch: true)
    repoFile(remoteGrgit, '1.txt') << '1.5.1'
    remoteGrgit.commit(message: 'do', all: true)
    remoteGrgit.checkout(branch: 'master')

    localGrgit = clone('local', remoteGrgit)
    localGrgit.checkout(branch: 'my-branch', createBranch: true)

    repoFile(localGrgit, '1.txt') << '1.5'
    localGrgit.commit(message: 'do', all: true)

    localGrgit.tag.add(name: 'tag1')

    localGrgit.checkout(branch: 'master')

    repoFile(localGrgit, '1.txt') << '2'
    localGrgit.commit(message: 'do', all: true)

    localGrgit.tag.add(name: 'tag2')
  }

  def 'push to non-existent remote fails'() {
    when:
    localGrgit.push(remote: 'fake')
    then:
    thrown(GitAPIException)
  }

  def 'push without other settings pushes correct commits'() {
    when:
    localGrgit.push()
    then:
    GitTestUtil.resolve(localGrgit, 'refs/heads/master') == GitTestUtil.resolve(remoteGrgit, 'refs/heads/master')
    GitTestUtil.resolve(localGrgit, 'refs/heads/my-branch') != GitTestUtil.resolve(remoteGrgit, 'refs/heads/my-branch')
    !GitTestUtil.tags(remoteGrgit)
  }

  def 'push with all true pushes all branches'() {
    when:
    localGrgit.push(all: true)
    then:
    GitTestUtil.resolve(localGrgit, 'refs/heads/master') == GitTestUtil.resolve(remoteGrgit, 'refs/heads/master')
    GitTestUtil.resolve(localGrgit, 'refs/heads/my-branch') == GitTestUtil.resolve(remoteGrgit, 'refs/heads/my-branch')
    !GitTestUtil.tags(remoteGrgit)
  }

  def 'push with tags true pushes all tags'() {
    when:
    localGrgit.push(tags: true)
    then:
    GitTestUtil.resolve(localGrgit, 'refs/heads/master') != GitTestUtil.resolve(remoteGrgit, 'refs/heads/master')
    GitTestUtil.resolve(localGrgit, 'refs/heads/my-branch') != GitTestUtil.resolve(remoteGrgit, 'refs/heads/my-branch')
    GitTestUtil.tags(localGrgit) == GitTestUtil.tags(remoteGrgit)
  }

  def 'push with refs only pushes those refs'() {
    when:
    localGrgit.push(refsOrSpecs: ['my-branch'])
    then:
    GitTestUtil.resolve(localGrgit, 'refs/heads/master') != GitTestUtil.resolve(remoteGrgit, 'refs/heads/master')
    GitTestUtil.resolve(localGrgit, 'refs/heads/my-branch') == GitTestUtil.resolve(remoteGrgit, 'refs/heads/my-branch')
    !GitTestUtil.tags(remoteGrgit)
  }

  def 'push with refSpecs only pushes those refs'() {
    when:
    localGrgit.push(refsOrSpecs: ['+refs/heads/my-branch:refs/heads/other-branch'])
    then:
    GitTestUtil.resolve(localGrgit, 'refs/heads/master') != GitTestUtil.resolve(remoteGrgit, 'refs/heads/master')
    GitTestUtil.resolve(localGrgit, 'refs/heads/my-branch') != GitTestUtil.resolve(remoteGrgit, 'refs/heads/my-branch')
    GitTestUtil.resolve(localGrgit, 'refs/heads/my-branch') == GitTestUtil.resolve(remoteGrgit, 'refs/heads/other-branch')
    !GitTestUtil.tags(remoteGrgit)
  }

  def 'push with non-fastforward fails'() {
    when:
    localGrgit.push(refsOrSpecs: ['refs/heads/master:refs/heads/some-branch'])
    then:
    GitTestUtil.resolve(localGrgit, 'refs/heads/master') != GitTestUtil.resolve(remoteGrgit, 'refs/heads/some-branch')
    thrown(PushException)
  }

  def 'push in dryRun mode does not push commits'() {
    given:
    def remoteMasterHead = GitTestUtil.resolve(remoteGrgit, 'refs/heads/master')
    when:
    localGrgit.push(dryRun: true)
    then:
    GitTestUtil.resolve(localGrgit, 'refs/heads/master') != GitTestUtil.resolve(remoteGrgit, 'refs/heads/master')
    GitTestUtil.resolve(remoteGrgit, 'refs/heads/master') == remoteMasterHead
  }

  def 'push in dryRun mode does not push tags'() {
    given:
    def remoteMasterHead = GitTestUtil.resolve(remoteGrgit, 'refs/heads/master')
    when:
    localGrgit.push(dryRun: true, tags: true)
    then:
    !GitTestUtil.tags(remoteGrgit)
  }
}
