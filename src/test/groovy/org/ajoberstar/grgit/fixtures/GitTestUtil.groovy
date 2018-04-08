package org.ajoberstar.grgit.fixtures

import org.ajoberstar.grgit.Branch
import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.api.ListBranchCommand.ListMode
import org.eclipse.jgit.transport.RemoteConfig

final class GitTestUtil {
  private GitTestUtil() {
    throw new AssertionError('Cannot instantiate this class.')
  }

  static File repoFile(Grgit grgit, String path, boolean makeDirs = true) {
    def file = new File(grgit.repository.rootDir, path)
    if (makeDirs) file.parentFile.mkdirs()
    return file
  }

  static File repoDir(Grgit grgit, String path) {
    def file = new File(grgit.repository.rootDir, path)
    file.mkdirs()
    return file
  }

  static Branch branch(String fullName, String trackingBranchFullName = null) {
    Branch trackingBranch = trackingBranchFullName ? branch(trackingBranchFullName) : null
    return new Branch(fullName, trackingBranch)
  }

  static List branches(Grgit grgit, boolean trim = false) {
    return grgit.repository.jgit.branchList().with {
      listMode = ListMode.ALL
      delegate.call()
    }.collect { trim ? it.name - 'refs/heads/' : it.name }
  }

  static List remoteBranches(Grgit grgit) {
    return grgit.repository.jgit.branchList().with {
      listMode = ListMode.REMOTE
      delegate.call()
    }.collect { it.name - 'refs/remotes/origin/' }
  }

  static List tags(Grgit grgit) {
    return grgit.repository.jgit.tagList().call().collect {
      it.name - 'refs/tags/'
    }
  }

  static List remotes(Grgit grgit) {
    def jgitConfig = grgit.repository.jgit.repo.config
    return RemoteConfig.getAllRemoteConfigs(jgitConfig).collect { it.name}
  }

  static Commit resolve(Grgit grgit, String revstr) {
    return JGitUtil.resolveCommit(grgit.repository, revstr)
  }

  static void configure(Grgit grgit, Closure closure) {
    def config = grgit.repository.jgit.repo.config
    config.with(closure)
    config.save()
  }
}
