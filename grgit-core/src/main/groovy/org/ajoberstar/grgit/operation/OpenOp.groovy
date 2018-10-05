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
  Credentials credentials

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
      def repo = new Repository(dirFile, Git.open(dirFile), credentials)
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
      Repository repo = new Repository(jgitRepo.directory, jgit, credentials)
      return new Grgit(repo)
    }
  }

  @Deprecated
  Credentials getCreds(){
    return credentials
  }

  @Deprecated
  void setCreds(Credentials creds){
    this.credentials = creds
  }
}
