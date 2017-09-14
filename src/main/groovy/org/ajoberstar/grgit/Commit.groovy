/*
 * Copyright 2012-2017 the original author or authors.
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
   * hash to (defaults to 7)
   */
  String getAbbreviatedId(int length = 7) {
    return id[0..(length - 1)]
  }
}
