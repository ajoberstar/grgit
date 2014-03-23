package org.ajoberstar.grgit.auth

import org.ajoberstar.grgit.exception.GrgitException

class AuthConfig {
	static final String FORCE_OPTION = 'org.ajoberstar.grgit.auth.force'

	final Set<Option> allowed

	private AuthConfig(Set<Option> allowed) {
		this.allowed = allowed.asImmutable()
	}

	boolean allows(Option option) {
		return allowed.contains(option)
	}

	static AuthConfig fromMap(Map properties) {
		String forceSetting = properties[FORCE_OPTION]
		if (forceSetting) {
			try {
				return new AuthConfig([Option.valueOf(forceSetting.toUpperCase())] as Set)
			} catch (IllegalArgumentException e) {
				throw new GrgitException("${FORCE_OPTION} must be set to one of ${Option.values() as List}. Currently set to: ${forceSetting}", e)
			}
		} else {
			Set<Option> allowed = (Option.values() as Set).findAll {
				String setting = properties[it.systemPropertyName]
				setting == null ? true : Boolean.valueOf(setting)
			}
			return new AuthConfig(allowed)
		}
	}

	static AuthConfig fromSystemProperties() {
		return fromMap(System.properties)
	}

	static enum Option {
		HARDCODED,
		INTERACTIVE,
		SSHAGENT,
		PAGEANT

		String getSystemPropertyName() {
			return "org.ajoberstar.grgit.auth.${name().toLowerCase()}.allow"
		}
	}
}
