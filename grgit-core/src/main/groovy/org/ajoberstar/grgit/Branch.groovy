package org.ajoberstar.grgit

import groovy.transform.Immutable

import org.eclipse.jgit.lib.Repository

/**
 * A branch.
 * @since 0.2.0
 */
@Immutable
class Branch {
  /**
   * The fully qualified name of this branch.
   */
  String fullName

  /**
   * This branch's upstream branch. {@code null} if this branch isn't
   * tracking an upstream.
   */
  Branch trackingBranch

  /**
   * The simple name of the branch.
   * @return the simple name
   */
  String getName() {
    return Repository.shortenRefName(fullName)
  }
}
