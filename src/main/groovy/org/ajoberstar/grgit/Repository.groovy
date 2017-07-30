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
