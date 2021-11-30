package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Credentials
import spock.lang.IgnoreIf

import java.nio.file.Files

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.errors.RepositoryNotFoundException
import org.eclipse.jgit.api.errors.GitAPIException

import spock.util.environment.RestoreSystemProperties

class OpenOpSpec extends SimpleGitOpSpec {
  private static final String FILE_PATH = 'the-dir/test.txt'
  Commit commit
  File subdir

  def setup() {
    repoFile(FILE_PATH) << '1.1'
    grgit.add(patterns: ['.'])
    commit = grgit.commit(message: 'first commit')
    subdir = repoDir('the-dir')
  }

  def 'open with dir fails if there is no repo in that dir'() {
    when:
    Grgit.open(dir: 'dir/with/no/repo')
    then:
    thrown(RepositoryNotFoundException)
  }

  def 'open with dir succeeds if repo is in that directory'() {
    when:
    Grgit opened = Grgit.open(dir: repoDir('.'))
    then:
    opened.head() == commit
  }

  @RestoreSystemProperties
  @IgnoreIf({ Integer.parseInt(System.properties['java.version'].split('\\.')[0]) >= 11})
  def 'open without dir fails if there is no repo in the current dir'() {
    given:
    File workingDir = new File(tempDir, 'no_repo')
    System.setProperty('user.dir', workingDir.absolutePath)
    when:
    Grgit.open()
    then:
    thrown(IllegalStateException)
  }

  @RestoreSystemProperties
  @IgnoreIf({ Integer.parseInt(System.properties['java.version'].split('\\.')[0]) >= 11})
  def 'open without dir succeeds if current directory is repo dir'() {
    given:
    File dir = repoDir('.')
    System.setProperty('user.dir', dir.absolutePath)
    when:
    Grgit opened = Grgit.open()
    repoFile(FILE_PATH) << '1.2'
    opened.add(patterns: [FILE_PATH])
    then:
    opened.head() == commit
    opened.status() == new Status(staged: [modified: [FILE_PATH]])
  }

  @RestoreSystemProperties
  @IgnoreIf({ Integer.parseInt(System.properties['java.version'].split('\\.')[0]) >= 11})
  def 'open without dir succeeds if current directory is subdir of a repo'() {
    given:
    System.setProperty('user.dir', subdir.absolutePath)
    when:
    Grgit opened = Grgit.open()
    repoFile(FILE_PATH) << '1.2'
    then:
    opened.head() == commit
    opened.status() == new Status(unstaged: [modified: [FILE_PATH]])
  }

  @RestoreSystemProperties
  @IgnoreIf({ Integer.parseInt(System.properties['java.version'].split('\\.')[0]) >= 11})
  def 'open without dir succeeds if .git in current dir has gitdir'() {
    given:
    File workDir = new File(tempDir, 'temp1')
    File gitDir = new File(tempDir, 'temp2')

    Git.cloneRepository()
      .setDirectory(workDir)
      .setGitDir(gitDir)
      .setURI(repoDir('.').toURI().toString())
      .call()

    new File(workDir, FILE_PATH) << '1.2'
    System.setProperty('user.dir', workDir.absolutePath)
    when:
    Grgit opened = Grgit.open()
    then:
    opened.head() == commit
    opened.status() == new Status(unstaged: [modified: [FILE_PATH]])
  }

  @RestoreSystemProperties
  @IgnoreIf({ Integer.parseInt(System.properties['java.version'].split('\\.')[0]) >= 11})
  def 'open without dir succeeds if .git in parent dir has gitdir'() {
    given:
    File workDir = new File(tempDir, 'temp1')
    File gitDir = new File(tempDir, 'temp2')

    Git.cloneRepository()
        .setDirectory(workDir)
        .setGitDir(gitDir)
        .setURI(repoDir('.').toURI().toString())
        .call()

    new File(workDir, FILE_PATH) << '1.2'
    System.setProperty('user.dir', new File(workDir, 'the-dir').absolutePath)
    when:
    Grgit opened = Grgit.open()
    then:
    opened.head() == commit
    opened.status() == new Status(unstaged: [modified: [FILE_PATH]])
  }

  def 'open with currentDir succeeds if current directory is subdir of a repo'() {
    when:
    Grgit opened = Grgit.open(currentDir: subdir)
    repoFile(FILE_PATH) << '1.2'
    then:
    opened.head() == commit
    opened.status() == new Status(unstaged: [modified: [FILE_PATH]])
  }

  def 'opened repo can be deleted after being closed'() {
    given:
    Grgit opened = Grgit.open(dir: repoDir('.').canonicalFile)
    when:
    opened.close()
    then:
    opened.repository.rootDir.deleteDir()
  }

  def 'credentials as param name should work'() {
    when:
    Grgit opened = Grgit.open(dir: repoDir('.'), credentials: new Credentials())
    then:
    opened.head() == commit
  }

  def 'creds as param name should work'() {
    when:
    Grgit opened = Grgit.open(dir: repoDir('.'), creds: new Credentials())
    then:
    opened.head() == commit
  }
}
