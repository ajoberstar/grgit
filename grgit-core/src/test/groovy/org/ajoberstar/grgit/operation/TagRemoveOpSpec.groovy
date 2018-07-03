package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec

class TagRemoveOpSpec extends SimpleGitOpSpec {
  def setup() {
    repoFile('1.txt') << '1'
    grgit.commit(message: 'do', all: true)
    grgit.tag.add(name: 'tag1')

    repoFile('1.txt') << '2'
    grgit.commit(message: 'do', all: true)
    grgit.tag.add(name: 'tag2', annotate: false)
  }

  def 'tag remove with empty list does nothing'() {
    expect:
    grgit.tag.remove() == []
    grgit.tag.list().collect { it.fullName } == ['refs/tags/tag1', 'refs/tags/tag2']
  }

  def 'tag remove with one tag removes tag'() {
    expect:
    grgit.tag.remove(names: ['tag2']) == ['refs/tags/tag2']
    grgit.tag.list().collect { it.fullName } == ['refs/tags/tag1']
  }

  def 'tag remove with multiple tags removes tags'() {
    expect:
    grgit.tag.remove(names: ['tag2', 'tag1']) as Set == ['refs/tags/tag2', 'refs/tags/tag1'] as Set
    grgit.tag.list() == []
  }

  def 'tag remove with invalid tags skips invalid and removes others'() {
    expect:
    grgit.tag.remove(names: ['tag2', 'blah4']) == ['refs/tags/tag2']
    grgit.tag.list().collect { it.fullName } == ['refs/tags/tag1']
  }
}
