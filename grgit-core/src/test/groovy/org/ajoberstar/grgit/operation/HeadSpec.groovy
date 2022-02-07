package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.fixtures.GitTestUtil

import spock.lang.TempDir

import spock.lang.Specification

class HeadSpec extends Specification {
  @TempDir
  File repoDir

  def 'head on a newly initialized repo returns null'() {
    given:
    def grgit = Grgit.init(dir: repoDir)
    expect:
    grgit.head() == null
  }
}
