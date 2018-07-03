package org.ajoberstar.grgit

import groovy.transform.Immutable

/**
 * The tracking status of a branch.
 * @since 0.2.0
 */
@Immutable
class BranchStatus {
  /**
   * The branch this object is for.
   */
  Branch branch

  /**
   * The number of commits this branch is ahead of its upstream.
   */
  int aheadCount

  /**
   * The number of commits this branch is behind its upstream.
   */
  int behindCount
}
