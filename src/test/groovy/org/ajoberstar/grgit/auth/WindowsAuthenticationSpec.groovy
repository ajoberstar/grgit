/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ajoberstar.grgit.auth

import org.ajoberstar.grgit.Credentials
import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Person
import org.ajoberstar.grgit.fixtures.GitTestUtil
import org.ajoberstar.grgit.fixtures.WindowsSpecific

import org.junit.Rule
import org.junit.experimental.categories.Category
import org.junit.rules.TemporaryFolder

import spock.lang.Specification
import spock.lang.Unroll

@Category(WindowsSpecific)
class WindowsAuthenticationSpec extends Specification {
  private static final String SSH_URI = 'git@github.com:ajoberstar/grgit-test.git'
  private static final String HTTPS_URI = 'https://github.com/ajoberstar/grgit-test.git'

  static Credentials hardcodedCreds
  static Credentials partialCreds

  @Rule TemporaryFolder tempDir = new TemporaryFolder()

  Grgit grgit
  Person person = new Person('Bruce Wayne', 'bruce.wayne@wayneindustries.com')


  def setupSpec() {
    def username = System.properties['org.ajoberstar.grgit.test.username']
    def password = System.properties['org.ajoberstar.grgit.test.password']
    hardcodedCreds = new Credentials(username, password)
    partialCreds = new Credentials(password, null)
    assert hardcodedCreds.username && hardcodedCreds.password
  }

  def cleanup() {
    System.properties.remove(AuthConfig.FORCE_OPTION)
    System.properties.remove(AuthConfig.USERNAME_OPTION)
    System.properties.remove(AuthConfig.PASSWORD_OPTION)
  }

  @Unroll('#method works using ssh? (#ssh) and creds? (#creds)')
  def 'auth method works'() {
    given:
    assert System.properties[AuthConfig.FORCE_OPTION] == null, 'Force should not already be set.'
    assert System.properties[AuthConfig.USERNAME_OPTION] == null, 'Username should not already be set.'
    assert System.properties[AuthConfig.PASSWORD_OPTION] == null, 'Password should not already be set.'
    System.properties[AuthConfig.FORCE_OPTION] = method
    ready(ssh)
    if (!creds) {
      System.properties[AuthConfig.USERNAME_OPTION] = hardcodedCreds.username
      System.properties[AuthConfig.PASSWORD_OPTION] = hardcodedCreds.password
    }
    assert grgit.branch.status(branch: 'master').aheadCount == 1
    when:
    grgit.push()
    then:
    grgit.branch.status(branch: 'master').aheadCount == 0
    where:
    method		    | ssh   | creds
    'hardcoded'   | false | hardcodedCreds
    'hardcoded'   | false | partialCreds
    'hardcoded'   | false | null
    'interactive' | false | null
    'interactive' | true  | null
    'sshagent'	  | true  | null
    'pageant'	    | true  | null
  }

  private void ready(boolean ssh) {
    File repoDir = tempDir.newFolder('repo')
    String uri = ssh ? SSH_URI : HTTPS_URI
    grgit = Grgit.clone(uri: uri, dir: repoDir)
    grgit.repository.jgit.repo.config.with {
      setString('user', null, 'name', person.name)
      setString('user', null, 'email', person.email)
      save()
    }

    repoFile('windows_test.txt') << "${new Date().format('yyyy-MM-dd')}\n"
    grgit.add(patterns: ['.'])
    grgit.commit(message: 'Windows auth test')
  }

  private File repoFile(String path, boolean makeDirs = true) {
    return GitTestUtil.repoFile(grgit, path, makeDirs)
  }
}
