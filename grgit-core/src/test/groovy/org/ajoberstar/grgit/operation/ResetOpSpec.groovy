package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec

class ResetOpSpec extends SimpleGitOpSpec {
  List commits = []

  def setup() {
    repoFile('1.bat') << '1'
    repoFile('something/2.txt') << '2'
    repoFile('test/3.bat') << '3'
    repoFile('test/4.txt') << '4'
    repoFile('test/other/5.txt') << '5'
    grgit.add(patterns:['.'])
    commits << grgit.commit(message: 'Test')
    repoFile('1.bat') << '2'
    repoFile('test/3.bat') << '4'
    grgit.add(patterns:['.'])
    commits << grgit.commit(message: 'Test')
    repoFile('1.bat') << '3'
    repoFile('something/2.txt') << '2'
    grgit.add(patterns:['.'])
    repoFile('test/other/5.txt') << '6'
    repoFile('test/4.txt') << '5'
  }

  def 'reset soft changes HEAD only'() {
    when:
    grgit.reset(mode:'soft', commit:commits[0].id)
    then:
    commits[0] == grgit.head()
    grgit.status() == new Status(
      staged: [modified: ['1.bat', 'test/3.bat', 'something/2.txt']],
      unstaged: [modified: ['test/4.txt', 'test/other/5.txt']]
    )
  }

  def 'reset mixed changes HEAD and index'() {
    when:
    grgit.reset(mode:'mixed', commit:commits[0].id)
    then:
    commits[0] == grgit.head()
    grgit.status() == new Status(
      unstaged: [modified: ['1.bat', 'test/3.bat', 'test/4.txt', 'something/2.txt', 'test/other/5.txt']])
  }

  def 'reset hard changes HEAD, index, and working tree'() {
    when:
    grgit.reset(mode:'hard', commit:commits[0].id)
    then:
    commits[0] == grgit.head()
    grgit.status().clean
  }

  def 'reset with paths changes index only'() {
    when:
    grgit.reset(paths:['something/2.txt'])
    then:
    commits[1] == grgit.head()
    grgit.status() == new Status(
      staged: [modified: ['1.bat']],
      unstaged: [modified: ['test/4.txt', 'something/2.txt', 'test/other/5.txt']]
    )
  }

  def 'reset with paths and mode set not supported'() {
    when:
    grgit.reset(mode:'hard', paths:['.'])
    then:
    thrown(IllegalStateException)
  }
}
