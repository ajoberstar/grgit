package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.fixtures.GitTestUtil

import org.junit.Rule
import org.junit.rules.TemporaryFolder

import spock.lang.Specification

class HeadSpec extends Specification {
  @Rule TemporaryFolder tempDir = new TemporaryFolder()

  File repoDir

  def setup() {
    repoDir = tempDir.newFolder('repo')
  }

  def 'head on a newly initialized repo returns null'() {
    given:
    def grgit = Grgit.init(dir: repoDir)
    expect:
    grgit.head() == null
  }
}
