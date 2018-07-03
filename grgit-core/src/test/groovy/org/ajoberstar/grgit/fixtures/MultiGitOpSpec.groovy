package org.ajoberstar.grgit.fixtures

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Person

import org.eclipse.jgit.api.Git

import org.junit.Rule
import org.junit.rules.TemporaryFolder

import spock.lang.Specification

class MultiGitOpSpec extends Specification {
  @Rule TemporaryFolder tempDir = new TemporaryFolder()

  Person person = new Person('Bruce Wayne', 'bruce.wayne@wayneindustries.com')

  protected Grgit init(String name) {
    File repoDir = tempDir.newFolder(name).canonicalFile
    Git git = Git.init().setDirectory(repoDir).call()

    // Don't want the user's git config to conflict with test expectations
    git.repo.FS.userHome = null

    git.repo.config.with {
      setString('user', null, 'name', person.name)
      setString('user', null, 'email', person.email)
      save()
    }
    return Grgit.open(dir: repoDir)
  }

  protected Grgit clone(String name, Grgit remote) {
    File repoDir = tempDir.newFolder(name)
    return Grgit.clone {
      dir = repoDir
      uri = remote.repository.rootDir.toURI()
    }
  }

  protected File repoFile(Grgit grgit, String path, boolean makeDirs = true) {
    return GitTestUtil.repoFile(grgit, path, makeDirs)
  }
}
