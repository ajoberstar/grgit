package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Tag
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec

class TagListOpSpec extends SimpleGitOpSpec {
  List commits = []
  List tags = []

  def setup() {
    repoFile('1.txt') << '1'
    commits << grgit.commit(message: 'do', all: true)
    tags << grgit.tag.add(name: 'tag1', message: 'My message')

    repoFile('1.txt') << '2'
    commits << grgit.commit(message: 'do', all: true)
    tags << grgit.tag.add(name: 'tag2', message: 'My other\nmessage')

    tags << grgit.tag.add(name: 'tag3', message: 'My next message.', pointsTo: 'tag1')
  }

  def 'tag list lists all tags'() {
    expect:
    grgit.tag.list() == tags
  }
}
