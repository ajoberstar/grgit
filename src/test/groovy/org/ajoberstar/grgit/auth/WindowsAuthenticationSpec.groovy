package org.ajoberstar.grgit.auth

import spock.lang.Specification

import org.ajoberstar.grgit.fixtures.WindowsSpecific

import org.junit.experimental.categories.Category

@Category(WindowsSpecific)
class WindowsAuthenticationSpec extends Specification {
	def 'dummy'() {
		expect:
		false
	}
}
