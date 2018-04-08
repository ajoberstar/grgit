package org.ajoberstar.grgit.auth

import org.ajoberstar.grgit.fixtures.MacSpecific

import org.junit.experimental.categories.Category

import spock.lang.Specification

@Category(MacSpecific)
class MacAuthenticationSpec extends Specification {
  def 'dummy'() {
    expect:
    false
  }
}
