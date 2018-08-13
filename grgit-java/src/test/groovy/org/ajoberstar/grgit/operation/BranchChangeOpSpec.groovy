package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.fixtures.GitTestUtil
import org.ajoberstar.grgit.fixtures.MultiGitOpSpec
import spock.lang.Unroll

class BranchChangeOpSpec extends MultiGitOpSpec {
  Grgit localGrgit
  Grgit remoteGrgit

  List commits = []

  def setup() {
    remoteGrgit = init('remote')

    repoFile(remoteGrgit, '1.txt') << '1'
    commits << remoteGrgit.commit(message: 'do', all: true)

    repoFile(remoteGrgit, '1.txt') << '2'
    commits << remoteGrgit.commit(message: 'do', all: true)

    remoteGrgit.checkout(branch: 'my-branch', createBranch: true)

    repoFile(remoteGrgit, '1.txt') << '3'
    commits << remoteGrgit.commit(message: 'do', all: true)

    localGrgit = clone('local', remoteGrgit)
    localGrgit.branch.add(name: 'local-branch')

    localGrgit.branch.add(name: 'test-branch', startPoint: commits[0].id)
  }

  def 'branch change with non-existent branch fails'() {
    when:
    localGrgit.branch.change(name: 'fake-branch', startPoint: 'test-branch')
    then:
    thrown(IllegalStateException)
  }

  def 'branch change with no start point fails'() {
    when:
    localGrgit.branch.change(name: 'local-branch')
    then:
    thrown(IllegalArgumentException)
  }

  @Unroll('branch change with #mode mode starting at #startPoint tracks #trackingBranch')
  def 'branch change with mode and start point behaves correctly'() {
    expect:
    localGrgit.branch.change(name: 'local-branch', startPoint: startPoint, mode: mode) == GitTestUtil.branch('refs/heads/local-branch', trackingBranch)
    localGrgit.resolve.toCommit('local-branch') == localGrgit.resolve.toCommit(startPoint)
    where:
    mode                         | startPoint         | trackingBranch
    null                         | 'origin/my-branch' | 'refs/remotes/origin/my-branch'
    BranchChangeOp.Mode.TRACK    | 'origin/my-branch' | 'refs/remotes/origin/my-branch'
    BranchChangeOp.Mode.NO_TRACK | 'origin/my-branch' | null
    null                         | 'test-branch'      | null
    BranchChangeOp.Mode.TRACK    | 'test-branch'      | 'refs/heads/test-branch'
    BranchChangeOp.Mode.NO_TRACK | 'test-branch'      | null
  }
}
