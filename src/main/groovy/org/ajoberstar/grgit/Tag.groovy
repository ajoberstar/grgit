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
