/*
 * Copyright 2012-2014 the original author or authors.
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
