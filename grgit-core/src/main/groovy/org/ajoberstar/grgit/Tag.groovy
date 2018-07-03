package org.ajoberstar.grgit

import groovy.transform.Immutable

import java.time.ZonedDateTime

import org.eclipse.jgit.lib.Repository

/**
 * A tag.
 * @since 0.2.0
 */
@Immutable(knownImmutableClasses=[ZonedDateTime])
class Tag {
  /**
   * The commit this tag points to.
   */
  Commit commit

  /**
   * The person who created the tag.
   */
  Person tagger

  /**
   * The full name of this tag.
   */
  String fullName

  /**
   * The full tag message.
   */
  String fullMessage

  /**
   * The shortened tag message.
   */
  String shortMessage

  /**
   * The time the commit was created with the time zone of the committer, if available.
   */
  ZonedDateTime dateTime

  /**
   * The simple name of this tag.
   * @return the simple name
   */
  String getName() {
    return Repository.shortenRefName(fullName)
  }
}
