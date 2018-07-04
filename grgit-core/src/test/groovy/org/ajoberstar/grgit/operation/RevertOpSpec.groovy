package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec

class RevertOpSpec extends SimpleGitOpSpec {
  List commits = []

  def setup() {
    5.times {
      repoFile("${it}.txt") << "1"
      grgit.add(patterns:['.'])
      commits << grgit.commit(message:'Test', all: true)
    }
  }

  def 'revert with no commits does nothing'() {
    when:
    grgit.revert()
    then:
    grgit.log().size() == 5
  }

  def 'revert with commits removes associated changes'() {
    when:
    grgit.revert(commits:[1, 3].collect { commits[it].id })
    then:
    grgit.log().size() == 7
    repoFile('.').listFiles().collect { it.name }.findAll { !it.startsWith('.') } as Set == [0, 2, 4].collect { "${it}.txt" } as Set
  }

  def 'revert with conflicts raises exception'() {
    given:
    repoFile("1.txt") << "Edited"
    grgit.add(patterns:['.'])
    commits << grgit.commit(message:'Modified', all: true)
    when:
    grgit.revert(commits:[1, 3].collect { commits[it].id })
    then:
    thrown(IllegalStateException)
    grgit.log().size() == 6
    grgit.status().conflicts.containsAll('1.txt')
  }
}
