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
