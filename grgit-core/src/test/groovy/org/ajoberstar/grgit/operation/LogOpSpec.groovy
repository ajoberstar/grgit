package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.merge.MergeStrategy

class LogOpSpec extends SimpleGitOpSpec {
  List commits = []

  def intToCommit = { commits[it] }

  def setup() {
    // TODO: Convert to Grgit when merge available
    File testFile1 = repoFile('1.txt')
    File testFile2 = repoFile('2.txt')

    testFile1 << '1'
    testFile2 << '2.1'
    grgit.add(patterns: ['.'])
    commits << grgit.commit(message: 'first commit\ntesting')

    testFile1 << '2'
    grgit.add(patterns: ['.'])
    commits << grgit.commit(message: 'second commit')
    grgit.tag.add(name: 'v1.0.0', message: 'annotated tag')

    grgit.checkout(branch: intToCommit(0).id)
    testFile1 << '3'
    grgit.add(patterns: ['.'])
    commits << grgit.commit(message: 'third commit')

    grgit.checkout(branch: 'master')
    def jgitId = JGitUtil.resolveObject(grgit.repository, commits[2].id)
    def mergeCommit = grgit.repository.jgit.merge().include(jgitId).setStrategy(MergeStrategy.OURS).call().newHead
    commits << JGitUtil.convertCommit(grgit.repository, mergeCommit)

    testFile1 << '4'
    grgit.add(patterns: ['.'])
    commits << grgit.commit(message: 'fifth commit')

    testFile2 << '2.2'
    grgit.add(patterns: ['.'])
    commits << grgit.commit(message: 'sixth commit')
  }

  def 'log with no arguments returns all commits'() {
    expect:
    grgit.log() in [[5, 4, 3, 2, 1, 0], [5, 4, 3, 1, 2, 0]]*.collect(intToCommit)
  }

  def 'log with max commits returns that number of commits'() {
    expect:
    grgit.log(maxCommits:2) == [5, 4].collect(intToCommit)
  }

  def 'log with skip commits does not return the first x commits'() {
    expect:
    grgit.log(skipCommits:2) in [[3, 2, 1, 0], [3, 1, 2, 0]]*.collect(intToCommit)
  }

  def 'log with range returns only the commits in that range'() {
    expect:
    grgit.log {
      range intToCommit(2).id, intToCommit(4).id
    } == [4, 3, 1].collect(intToCommit)
  }

  def 'log with non-existing commit fails'() {
    when:
    grgit.log(includes: ['garbage', intToCommit(1)])
    then:
    thrown(IllegalArgumentException)
  }

  def 'log with path includes only commits with changes for that path'() {
    expect:
    grgit.log(paths:['2.txt']).collect { it.id } == [5, 0].collect(intToCommit).collect { it.id }
  }

  def 'log with annotated tag short name works'() {
    expect:
    grgit.log(includes: ['v1.0.0']) == [1, 0].collect(intToCommit)
  }
}
