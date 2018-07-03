package org.ajoberstar.grgit

import groovy.transform.Immutable

import org.eclipse.jgit.lib.Repository

/**
 * A ref.
 * @since 2.0.0
 */
@Immutable
class Ref {
  /**
   * The fully qualified name of this ref.
   */
  String fullName

  /**
   * The simple name of the ref.
   * @return the simple name
   */
  String getName() {
    return Repository.shortenRefName(fullName)
  }
}
