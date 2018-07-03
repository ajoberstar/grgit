package org.ajoberstar.grgit.auth

import org.ajoberstar.grgit.Credentials

import spock.lang.Specification
import spock.lang.Unroll

class AuthConfigSpec extends Specification {
  def 'getHardcodedCreds returns creds if username and password are set with properties'() {
    given:
    def props = [(AuthConfig.USERNAME_OPTION): 'myuser', (AuthConfig.PASSWORD_OPTION): 'mypass']
    expect:
    AuthConfig.fromMap(props).getHardcodedCreds() == new Credentials('myuser', 'mypass')
  }

  def 'getHardcodedCreds returns creds if username and password are set with env'() {
    given:
    def env = [(AuthConfig.USERNAME_ENV_VAR): 'myuser', (AuthConfig.PASSWORD_ENV_VAR): 'mypass']
    expect:
    AuthConfig.fromMap([:], env).getHardcodedCreds() == new Credentials('myuser', 'mypass')
  }

  def 'getHardcodedCreds returns creds if username is set and password is not'() {
    given:
    def props = [(AuthConfig.USERNAME_OPTION): 'myuser']
    expect:
    AuthConfig.fromMap(props).getHardcodedCreds() == new Credentials('myuser', null)
  }

  def 'getHardcodedCreds are not populated if username is not set'() {
    expect:
    !AuthConfig.fromMap([:]).getHardcodedCreds().isPopulated()
  }
}
