package org.ajoberstar.grgit

import groovy.transform.Immutable

/**
 * A person.
 * @since 0.1.0
 */
 @Immutable
class Person {
  /**
   * Name of person.
   */
  String name

  /**
   * Email address of person.
   */
  String email
}
