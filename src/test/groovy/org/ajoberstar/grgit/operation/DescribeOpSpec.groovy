package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec

class DescribeOpSpec extends SimpleGitOpSpec {
  def setup() {
    grgit.commit(message:"initial commit")
    grgit.tag.add(name:"initial")
    grgit.commit(message:"another commit")
    grgit.tag.add(name:"another")
  }

  def 'with tag'() {
    expect:
    grgit.describe() == "another"
  }

  def 'with additional commit'(){
    when:
    repoFile('1.txt') << '1'
    grgit.add(patterns:['1.txt'])
    grgit.commit(message:  "another commit")
    then:
    grgit.describe().startsWith("another-1-")
  }

  def 'from differnt commit'(){
    when:
    repoFile('1.txt') << '1'
    grgit.add(patterns:['1.txt'])
    grgit.commit(message:  "another commit")
    then:
    grgit.describe(commit: 'HEAD~2') == "initial"
  }

  def 'with long description'() {
    expect:
    grgit.describe(longDescr: true).startsWith("another-0-")
  }

  def 'with match'() {
    expect:
    grgit.describe(match: ['initial*']).startsWith("initial-1-")
  }
}
