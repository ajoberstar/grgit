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
package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Credentials
import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.util.CoercionUtil
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

/**
 * Opens an existing repository. Returns a {@link Grgit} pointing
 * to the resulting repository.
 * @since 1.0.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-open.html">grgit-open</a>
 */
@Operation('open')
class OpenOp implements Callable<Grgit> {
  /**
   * Hardcoded credentials to use for remote operations.
   */
  Credentials creds

  /**
   * The directory to open the repository from. Incompatible
   * with {@code currentDir}.
   * @see {@link CoercionUtil#toFile(Object)}
   */
  Object dir

  /**
   * The directory to begin searching from the repository
   * from. Incompatible with {@code dir}.
   * @see {@link CoercionUtil#toFile(Object)}
   */
  Object currentDir

  Grgit call() {
    if (dir && currentDir) {
      throw new IllegalArgumentException('Cannot use both dir and currentDir.')
    } else if (dir) {
      def dirFile = CoercionUtil.toFile(dir)
      def repo = new Repository(dirFile, Git.open(dirFile), creds)
      return new Grgit(repo)
    } else {
      FileRepositoryBuilder builder = new FileRepositoryBuilder()
      builder.readEnvironment()
      if (currentDir) {
        File currentDirFile = CoercionUtil.toFile(currentDir)
        builder.findGitDir(currentDirFile)
      } else {
        builder.findGitDir()
      }

      if(builder.getGitDir() == null){
        throw new IllegalStateException('No .git directory found!');
      }

      FileRepository jgitRepo = builder.build()
      Git jgit = new Git(jgitRepo)
      Repository repo = new Repository(jgitRepo.directory, jgit, creds)
      return new Grgit(repo)
    }
  }
}
