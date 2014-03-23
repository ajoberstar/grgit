package org.ajoberstar.grgit.auth

import spock.lang.Specification

import org.ajoberstar.grgit.fixtures.LinuxSpecific

import org.junit.experimental.categories.Category

@Category(LinuxSpecific)
class LinuxAuthenticationSpec extends Specification {
	def 'dummy'() {
		expect:
		false
	}
}
