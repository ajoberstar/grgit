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
  def 'open without dir fails if there is no repo in the current dir'() {
    given:
    File workingDir = tempDir.newFolder('no_repo')
    System.setProperty('user.dir', workingDir.absolutePath)
    when:
    Grgit.open()
    then:
    thrown(IllegalStateException)
  }

  @RestoreSystemProperties
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
  def 'open without dir succeeds if .git in current dir has gitdir'() {
    given:
    File workDir = tempDir.newFolder()
    File gitDir = tempDir.newFolder()

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
  def 'open without dir succeeds if .git in parent dir has gitdir'() {
    given:
    File workDir = tempDir.newFolder()
    File gitDir = tempDir.newFolder()

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
}
