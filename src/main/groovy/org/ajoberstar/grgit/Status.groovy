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

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Status of the current working tree and index.
 */
@EqualsAndHashCode
@ToString(includeNames=true)
class Status {
  final Changes staged
  final Changes unstaged
  final Set<String> conflicts

  Status(Map args = [:]) {
    def invalidArgs = args.keySet() - ['staged', 'unstaged', 'conflicts']
    if (invalidArgs) {
      throw new IllegalArgumentException("Following keys are not supported: ${invalidArgs}")
    }
    this.staged = 'staged' in args ? new Changes(args.staged) : new Changes()
    this.unstaged = 'unstaged' in args ? new Changes(args.unstaged) : new Changes()
    this.conflicts = 'conflicts' in args ? args.conflicts : []
  }

  @EqualsAndHashCode
  @ToString(includeNames=true)
  class Changes {
    final Set<String> added
    final Set<String> modified
    final Set<String> removed

    Changes(Map args = [:]) {
      def invalidArgs = args.keySet() - ['added', 'modified', 'removed']
      if (invalidArgs) {
        throw new IllegalArgumentException("Following keys are not supported: ${invalidArgs}")
      }
      this.added = 'added' in args ? args.added : []
      this.modified = 'modified' in args ? args.modified : []
      this.removed = 'removed' in args ? args.removed : []
    }

    /**
     * Gets all changed files.
     * @return all changed files
     */
    Set<String> getAllChanges() {
      return added + modified + removed
    }
  }

  /**
   * Whether the repository has any changes or conflicts.
   * @return {@code true} if there are no changes either staged or unstaged or
   * any conflicts, {@code false} otherwise
   */
  boolean isClean() {
    return (staged.allChanges + unstaged.allChanges + conflicts).empty
  }
}
