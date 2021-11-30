package org.ajoberstar.grgit.fixtures

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Person

import org.eclipse.jgit.api.Git

import spock.lang.TempDir

import spock.lang.Specification

class SimpleGitOpSpec extends Specification {
  @TempDir
  File tempDir
  File repoDir

  Grgit grgit
  Person person = new Person('Bruce Wayne', 'bruce.wayne@wayneindustries.com')

  def setup() {
    repoDir = new File(tempDir, 'repo')
    Git git = Git.init()
      .setDirectory(repoDir)
      .setInitialBranch('master') // for compatibility with existing tests
      .call()

    // Don't want the user's git config to conflict with test expectations
    git.repo.FS.userHome = null

    git.repo.config.with {
      setString('user', null, 'name', person.name)
      setString('user', null, 'email', person.email)
      save()
    }
    grgit = Grgit.open(dir: repoDir)
  }

  def cleanup() {
    grgit.close()
  }

  protected File repoFile(String path, boolean makeDirs = true) {
    return GitTestUtil.repoFile(grgit, path, makeDirs)
  }

  protected File repoDir(String path) {
    return GitTestUtil.repoDir(grgit, path)
  }
}
