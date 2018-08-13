package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec

class DescribeOpSpec extends SimpleGitOpSpec {
  def setup() {
    grgit.commit(message:'initial commit')
    grgit.tag.add(name:'initial')
    grgit.commit(message:'another commit')
    grgit.tag.add(name:'another')
    grgit.commit(message:'other commit')
    grgit.tag.add(name:'other', annotate: false)

  }

  def 'with tag'() {
    given:
    grgit.reset(commit: 'HEAD~1', mode: 'hard')
    expect:
    grgit.describe() == 'another'
  }

  def 'with additional commit'(){
    given:
    repoFile('1.txt') << '1'
    grgit.add(patterns:['1.txt'])
    grgit.commit(message: 'another commit')
    expect:
    grgit.describe().startsWith('another-2-')
  }

  def 'from different commit'(){
    given:
    repoFile('1.txt') << '1'
    grgit.add(patterns:['1.txt'])
    grgit.commit(message:  'another commit')
    expect:
    grgit.describe(commit: 'HEAD~3') == 'initial'
  }

  def 'with long description'() {
    expect:
    grgit.describe(longDescr: true).startsWith('another-1-')
  }

  def 'with un-annotated tags'() {
    expect:
    grgit.describe(tags: true) == 'other'
  }

  def 'with match'() {
    expect:
    grgit.describe(match: ['initial*']).startsWith('initial-2-')
  }
}
