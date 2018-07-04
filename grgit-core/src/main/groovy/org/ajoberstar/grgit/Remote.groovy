package org.ajoberstar.grgit

import groovy.transform.Immutable

/**
 * Remote repository.
 * @since 0.4.0
 */
@Immutable
class Remote {
  /**
   * Name of the remote.
   */
  String name

  /**
   * URL to fetch from.
   */
  String url

  /**
   * URL to push to.
   */
  String pushUrl

  /**
   * Specs to fetch from the remote.
   */
  List fetchRefSpecs = []

  /**
   * Specs to push to the remote.
   */
  List pushRefSpecs = []

  /**
   * Whether or not pushes will mirror the repository.
   */
  boolean mirror
}
