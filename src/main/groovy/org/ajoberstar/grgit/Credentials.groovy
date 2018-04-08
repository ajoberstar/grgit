package org.ajoberstar.grgit

import groovy.transform.Canonical

/**
 * Credentials to use for remote operations.
 * @since 0.2.0
 */
@Canonical
class Credentials {
  final String username
  final String password

  String getUsername() {
    return username ?: ''
  }

  String getPassword() {
    return password ?: ''
  }

  boolean isPopulated() {
    return username != null
  }
}
