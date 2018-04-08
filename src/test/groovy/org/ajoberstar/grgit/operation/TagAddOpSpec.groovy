package org.ajoberstar.grgit.operation

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoField

import org.ajoberstar.grgit.Tag
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec
import org.eclipse.jgit.api.errors.GitAPIException

class TagAddOpSpec extends SimpleGitOpSpec {
  List commits = []

  def setup() {
    repoFile('1.txt') << '1'
    commits << grgit.commit(message: 'do', all: true)

    repoFile('1.txt') << '2'
    commits << grgit.commit(message: 'do', all: true)

    repoFile('1.txt') << '3'
    commits << grgit.commit(message: 'do', all: true)
  }

  def 'tag add creates annotated tag pointing to current HEAD'() {
    given:
    Instant instant = Instant.now().with(ChronoField.NANO_OF_SECOND, 0)
    ZoneId zone = ZoneId.ofOffset('GMT', ZoneId.systemDefault().getRules().getOffset(instant))
    ZonedDateTime tagTime = ZonedDateTime.ofInstant(instant, zone)
    when:
    grgit.tag.add(name: 'test-tag')
    then:
    grgit.tag.list() == [new Tag(
      commits[2],
      person,
      'refs/tags/test-tag',
      '',
      '',
      tagTime
    )]
    grgit.resolve.toCommit('test-tag') == grgit.head()
  }

  def 'tag add with annotate false creates unannotated tag pointing to current HEAD'() {
    when:
    grgit.tag.add(name: 'test-tag', annotate: false)
    then:
    grgit.tag.list() == [new Tag(
      commits[2],
      null,
      'refs/tags/test-tag',
      null,
      null,
    null
    )]
    grgit.resolve.toCommit('test-tag') == grgit.head()
  }

  def 'tag add with name and pointsTo creates tag pointing to pointsTo'() {
    given:
    Instant instant = Instant.now().with(ChronoField.NANO_OF_SECOND, 0)
    ZoneId zone = ZoneId.ofOffset('GMT', ZoneId.systemDefault().getRules().getOffset(instant))
    ZonedDateTime tagTime = ZonedDateTime.ofInstant(instant, zone)
    when:
    grgit.tag.add(name: 'test-tag', pointsTo: commits[0].id)
    then:
    grgit.tag.list() == [new Tag(
      commits[0],
      person,
      'refs/tags/test-tag',
      '',
      '',
      tagTime
    )]
    grgit.resolve.toCommit('test-tag') == commits[0]
  }

  def 'tag add without force fails to overwrite existing tag'() {
    given:
    grgit.tag.add(name: 'test-tag', pointsTo: commits[0].id)
    when:
    grgit.tag.add(name: 'test-tag')
    then:
    thrown(GitAPIException)
  }

  def 'tag add with force overwrites existing tag'() {
    given:
    grgit.tag.add(name: 'test-tag', pointsTo: commits[0].id)
    when:
    grgit.tag.add(name: 'test-tag', force: true)
    then:
    grgit.resolve.toCommit('test-tag') == grgit.head()
  }
}
