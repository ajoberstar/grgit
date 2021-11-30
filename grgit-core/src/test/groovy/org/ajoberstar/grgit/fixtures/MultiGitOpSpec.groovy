package org.ajoberstar.grgit.fixtures

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Person

import org.eclipse.jgit.api.Git

import spock.lang.TempDir

import spock.lang.Specification

class MultiGitOpSpec extends Specification {
  @TempDir
  File tempDir

  Person person = new Person('Bruce Wayne', 'bruce.wayne@wayneindustries.com')

  private List<Grgit> grgits = []

  def cleanup() {
    grgits.each { it.close() }
  }

  protected Grgit init(String name) {
    File repoDir = new File(tempDir, name).canonicalFile
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
    def grgit = Grgit.open(dir: repoDir)
    grgits.add(grgit)
    return grgit
  }

  protected Grgit clone(String name, Grgit remote) {
    File repoDir = new File(tempDir, name)
    def grgit = Grgit.clone {
      dir = repoDir
      uri = remote.repository.rootDir.toURI()
    }
    grgits.add(grgit)
    return grgit
  }

  protected File repoFile(Grgit grgit, String path, boolean makeDirs = true) {
    return GitTestUtil.repoFile(grgit, path, makeDirs)
  }
}
