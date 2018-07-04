package org.ajoberstar.grgit

import groovy.transform.Immutable
import java.time.ZonedDateTime

/**
 * A commit.
 * @since 0.1.0
 */
 @Immutable(knownImmutableClasses=[ZonedDateTime])
class Commit {
  /**
   * The full hash of the commit.
   */
  String id

  /**
   * The abbreviated hash of the commit.
   */
  String abbreviatedId

  /**
   * Hashes of any parent commits.
   */
  List<String> parentIds

  /**
   * The author of the changes in the commit.
   */
  Person author

  /**
   * The committer of the changes in the commit.
   */
  Person committer

  /**
   * The time the commit was created with the time zone of the committer, if available.
   */
  ZonedDateTime dateTime

  /**
   * The full commit message.
   */
  String fullMessage

  /**
   * The shortened commit message.
   */
  String shortMessage

  /**
   * The time the commit was created in seconds since "the epoch".
   * @return the time
   * @deprecated use Commit#dateTime
   */
  @Deprecated
  long getTime() {
    return dateTime.toEpochSecond()
  }

  /**
   * The time the commit was created.
   * @return the date
   * @deprecated use Commit#dateTime
   */
  @Deprecated
  Date getDate() {
    return Date.from(dateTime.toInstant())
  }

  /**
   * The first {@code length} characters of the commit hash.
   * @param length the number of characters to abbreviate the
   * hash.
   */
  @Deprecated
  String getAbbreviatedId(int length) {
    return id[0..(length - 1)]
  }
}
