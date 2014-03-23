package org.ajoberstar.grgit.auth

import spock.lang.Specification
import spock.lang.Unroll

import static org.ajoberstar.grgit.auth.AuthConfig.Option.*

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
}
