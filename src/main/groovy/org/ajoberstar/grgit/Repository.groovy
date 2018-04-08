package org.ajoberstar.grgit

import groovy.transform.Immutable

import org.eclipse.jgit.api.Git

/**
 * A repository.
 * @since 0.1.0
 */
@Immutable(knownImmutableClasses=[Git, File, Credentials])
class Repository {
  /**
   * The directory the repository is contained in.
   */
  File rootDir

  /**
   * The JGit instance opened for this repository.
   */
  Git jgit

  /**
   * The credentials used when talking to remote repositories.
   */
  Credentials credentials

  @Override
  String toString() {
    return "Repository(${rootDir.canonicalPath})"
  }
}
