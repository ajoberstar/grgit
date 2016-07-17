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

import org.ajoberstar.grgit.Credentials
import org.ajoberstar.grgit.exception.GrgitException

/**
 * Stores configuration options for how to authenticate with remote
 * repositories.
 *
 * <p>
 *   The following system properties can be set to configure how
 *   authentication is performed with remote repositories. All "allow"
 *   properties default to {@code true}.
 * </p>
 *
 * <ul>
 *   <li>{@code org.ajoberstar.grgit.auth.force={hardcoded|interactive|sshagent|pageant}}</li>
 *   <li>{@code org.ajoberstar.grgit.auth.hardcoded.allow={true|false}}</li>
 *   <li>{@code org.ajoberstar.grgit.auth.interactive.allow={true|false}}</li>
 *   <li>{@code org.ajoberstar.grgit.auth.sshagent.allow={true|false}}</li>
 *   <li>{@code org.ajoberstar.grgit.auth.pageant.allow={true|false}}</li>
 * </ul>
 *
 * <p>
 *   In order to set default hardocded credentials, use the following properties.
 *   Note that unless hardcoded credentials are disabled, using these properties
 *   will supersede the use of interactive creds, ssh-agent, or Pageant. However,
 *   they will not take precedence over credentials provided directly to a
 *   repository during the clone, init, or open.
 * </p>
 *
 * <ul>
 *   <li>{@code org.ajoberstar.grgit.auth.username=<username>}</li>
 *   <li>{@code org.ajoberstar.grgit.auth.password=<password>}</li>
 * </ul>
 *
 * <p>
 *   In order to add a non-standard SSH key to use as your credentials,
 *   use the following property.
 * </p>
 *
 * <ul>
 *     <li>{@code org.ajoberstar.grgit.auth.ssh.private=<path.to.private.key>}</li>
 * </ul>
 * <p>
 *   In order to customize the JSch session config use a property of the
 *   following format (possible values <a href="http://epaul.github.io/jsch-documentation/javadoc/com/jcraft/jsch/JSch.html#setConfig(java.lang.String, java.lang.String)">JSch documentation</a>):
 * </p>
 *
 * <ul>
 *     <li>{@code org.ajoberstar.grgit.auth.session.config.<key>=<value>}</li>
 * </ul>
 *
 * <p>
 *   The following order is used to determine which authentication option
 *   is used.
 * </p>
 *
 * <ol>
 *   <li>Hardcoded credentials, if provided.</li>
 *   <li>Ssh-Agent, if available.</li>
 *   <li>Pageant, if available.</li>
 *   <li>Interactive credentials, if needed.</li>
 * </ol>
 *
 * @since 0.2.0
 */
class AuthConfig {
    /**
     * System property name used to force a specific authentication option.
     */
    static final String FORCE_OPTION = 'org.ajoberstar.grgit.auth.force'
    static final String USERNAME_OPTION = 'org.ajoberstar.grgit.auth.username'
    static final String PASSWORD_OPTION = 'org.ajoberstar.grgit.auth.password'
    static final String SSH_PRIVATE_KEY_OPTION = 'org.ajoberstar.grgit.auth.ssh.private'
    static final String SSH_SESSION_CONFIG_OPTION_PREFIX = 'org.ajoberstar.grgit.auth.session.config.'

    /**
     * Set of all authentication options that are allowed in this
     * configuration.
     */
    final Set<Option> allowed

    private AuthConfig(Set<Option> allowed) {
        this.allowed = allowed.asImmutable()
    }

    /**
     * Test whether the given authentication option is allowed by this
     * configuration.
     * @param option the authentication option to test for
     * @return {@code true} if the given option is allowed, {@code false}
     * otherwise
     */
    boolean allows(Option option) {
        return allowed.contains(option)
    }

    /**
     * Constructs and returns a {@link Credentials} instance reflecting the
     * settings in the system properties.
     * @return a credentials instance reflecting the settings in the system
     * properties, or, if the username isn't set, {@code null}
     */
    Credentials getHardcodedCreds() {
        String username = System.properties[USERNAME_OPTION]
        String password = System.properties[PASSWORD_OPTION]
        if (username) {
            return new Credentials(username, password)
        } else {
            return null
        }
    }

    /**
     * Gets the path to your SSH private key to use during authentication reflecting
     * the value set in the system properties.
     * @return the path to the SSH key, if set, otherwise {@code null}
     */
    String getSshPrivateKeyPath() {
        return System.properties[SSH_PRIVATE_KEY_OPTION]
    }

    /**
     * Gets session config override for SSH session that is used underneath by JGit
     * @return map with configuration or empty if nothing was specified in system property
     */
    Map<String, String> getSessionConfig() {
        return System.properties
            .findAll { key, value -> key.startsWith(SSH_SESSION_CONFIG_OPTION_PREFIX) }
            .collectEntries { key, value -> [key.substring(SSH_SESSION_CONFIG_OPTION_PREFIX.length()), value] }
    }

    /**
     * Factory method to construct an authentication configuration from the
     * given properties.
     * @param properties the properties to use in this configuration
     * @return the constructed configuration
     * @throws GrgitException if force is set to an invalid option
     */
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

    /**
     * Factory method to construct an authentication configuration from the
     * current system properties.
     * @return the constructed configuration
     * @throws GrgitException if force is set to an invalid option
     */
    static AuthConfig fromSystemProperties() {
        return fromMap(System.properties)
    }

    /**
     * Available authentication options.
     */
    static enum Option {
        /**
         * Use credentials provided directly to Grgit.
         */
        HARDCODED,

        /**
         * Will prompt for credentials using an AWT window, if needed.
         */
        INTERACTIVE,

        /**
         * Use SSH keys in the system's sshagent process.
         */
        SSHAGENT,

        /**
         * Use SSH keys in the system's pageant process.
         */
        PAGEANT

        /**
         * Gets the system property name used to configure whether this
         * option is allowed or not. By default, all are allowed.
         * The system properties are of the form
         * {@code org.ajoberstar.grgit.auth.<lowercase option name>.allow}
         * Can be set to {@code true} or {@code false}.
         * @return the system property name
         */
        String getSystemPropertyName() {
            return "org.ajoberstar.grgit.auth.${name().toLowerCase()}.allow"
        }
    }
}
