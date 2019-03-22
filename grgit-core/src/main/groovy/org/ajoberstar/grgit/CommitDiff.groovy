package org.ajoberstar.grgit

import groovy.transform.Immutable
import groovy.transform.ToString

@Immutable
@ToString(includeNames=true)
class CommitDiff {
  Commit commit

  Set<String> added = []

  Set<String> copied = []

  Set<String> modified = []

  Set<String> removed = []

  Set<String> renamed = []

  Map<String,String> renamings = [:]

  /**
   * Gets all changed files.
   * @return all changed files
   */
  Set<String> getAllChanges() {
    return added + copied + modified + removed + renamed
  }
}
