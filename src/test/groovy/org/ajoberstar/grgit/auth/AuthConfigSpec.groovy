/*
 * Copyright 2012-2015 the original author or authors.
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

import static org.ajoberstar.grgit.auth.AuthConfig.Option.*

import org.ajoberstar.grgit.Credentials

import spock.lang.Specification
import spock.lang.Unroll

class AuthConfigSpec extends Specification {
    @Unroll('with #properties only #allowed are allowed')
    def 'properties result in correct options allowed'() {
        given:
        def map = properties.collectEntries { key, value -> [("org.ajoberstar.grgit.auth.${key}".toString()): value] }
        def notAllowed = (AuthConfig.Option.values() as Set) - allowed
        when:
        def config = AuthConfig.fromMap(map)
        then:
        config.allowed == (allowed as Set)
        allowed.every { config.allows(it) }
        notAllowed.every { !config.allows(it) }
        where:
        properties                                                    | allowed
        [:]                                                           | [HARDCODED, INTERACTIVE, SSHAGENT, PAGEANT]
        [force: 'hardcoded']                                          | [HARDCODED]
        [force: 'INTERACTIVE']                                        | [INTERACTIVE]
        [force: 'ssHaGeNT']                                           | [SSHAGENT]
        [force: 'PaGeAnT']                                            | [PAGEANT]
        [force: 'hardcoded', 'hardcoded.allow': 'false']              | [HARDCODED]
        ['hardcoded.allow': 'false', 'interactive.allow': 'anything'] | [SSHAGENT, PAGEANT]
        ['pageant.allow': 'true', 'sshagent.allow': 'false']          | [HARDCODED, INTERACTIVE, PAGEANT]
    }

    def 'getHardcodedCreds returns creds if username and password are set'() {
        given:
        System.setProperty(AuthConfig.USERNAME_OPTION, 'myuser')
        System.setProperty(AuthConfig.PASSWORD_OPTION, 'mypass')
        expect:
        AuthConfig.fromMap([:]).getHardcodedCreds() == new Credentials('myuser', 'mypass')
    }

    def 'getHardcodedCreds returns creds if username is set and password is not'() {
        given:
        System.setProperty(AuthConfig.USERNAME_OPTION, 'myuser')
        System.clearProperty(AuthConfig.PASSWORD_OPTION)
        expect:
        AuthConfig.fromMap([:]).getHardcodedCreds() == new Credentials('myuser', null)
    }

    def 'getHardcodedCreds returns null if username is not set'() {
        given:
        System.clearProperty(AuthConfig.USERNAME_OPTION)
        System.clearProperty(AuthConfig.PASSWORD_OPTION)
        expect:
        AuthConfig.fromMap([:]).getHardcodedCreds() == null
    }

    def 'getSessionConfig returns empty map if nothing specified'() {
        expect:
        AuthConfig.fromMap(Collections.emptyMap()).sessionConfig.isEmpty()
    }

    def 'getSessionConfig returns session config based on system property'() {
        given:
        System.setProperty(AuthConfig.SSH_SESSION_CONFIG_OPTION_PREFIX + 'StrictHostKeyChecking', 'no')
        expect:
        AuthConfig.fromMap(Collections.emptyMap()).sessionConfig == [ StrictHostKeyChecking: 'no' ]
    }
}
