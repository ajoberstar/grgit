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

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.fixtures.MultiGitOpSpec
import org.eclipse.jgit.api.errors.GitAPIException

class PullOpSpec extends MultiGitOpSpec {
  Grgit localGrgit
  Grgit remoteGrgit
  Grgit otherRemoteGrgit
  Commit ancestorHead

  def setup() {
    remoteGrgit = init('remote')

    repoFile(remoteGrgit, '1.txt') << '1.1\n'
    remoteGrgit.add(patterns: ['.'])
    ancestorHead = remoteGrgit.commit(message: '1.1', all: true)

    remoteGrgit.branch.add(name: 'test-branch')

    localGrgit = clone('local', remoteGrgit)
    localGrgit.branch.add(name: 'test-branch', startPoint: 'origin/test-branch')

    otherRemoteGrgit = clone('remote2', remoteGrgit)
    repoFile(otherRemoteGrgit, '4.txt') << '4.1\n'
    otherRemoteGrgit.add(patterns: ['.'])
    otherRemoteGrgit.commit(message: '4.1', all: true)

    repoFile(remoteGrgit, '1.txt') << '1.2\n'
    remoteGrgit.commit(message: '1.2', all: true)
    repoFile(remoteGrgit, '1.txt') << '1.3\n'
    remoteGrgit.commit(message: '1.3', all: true)

    remoteGrgit.checkout(branch: 'test-branch')

    repoFile(remoteGrgit, '2.txt') << '2.1\n'
    remoteGrgit.add(patterns: ['.'])
    remoteGrgit.commit(message: '2.1', all: true)
    repoFile(remoteGrgit, '2.txt') << '2.2\n'
    remoteGrgit.commit(message: '2.2', all: true)
  }

  def 'pull to local repo with no changes fast-forwards current branch only'() {
    given:
    def localTestBranchHead = localGrgit.resolve.toCommit('test-branch')
    when:
    localGrgit.pull()
    then:
    localGrgit.head() == remoteGrgit.resolve.toCommit('master')
    localGrgit.resolve.toCommit('test-branch') == localTestBranchHead
  }

  def 'pull to local repo with clean changes merges branches from origin'() {
    given:
    repoFile(localGrgit, '3.txt') << '3.1\n'
    localGrgit.add(patterns: ['.'])
    localGrgit.commit(message: '3.1')
    def localHead = localGrgit.head()
    def remoteHead = remoteGrgit.resolve.toCommit('master')
    when:
    localGrgit.pull()
    then:
    // includes all commits from remote
    (remoteGrgit.log(includes: ['master']) - localGrgit.log()).size() == 0
    /*
     * Go back to one pass log command when bug is fixed:
     * https://bugs.eclipse.org/bugs/show_bug.cgi?id=439675
     */
    // localGrgit.log {
    //	 includes = [remoteHead.id]
    //	 excludes = ['HEAD']
    // }.size() == 0

    // has merge commit
    localGrgit.log {
      includes = ['HEAD']
      excludes = [localHead.id, remoteHead.id]
    }.size() == 1
  }

  def 'pull to local repo with conflicting changes fails'() {
    given:
    repoFile(localGrgit, '1.txt') << '1.4\n'
    localGrgit.commit(message: '1.4', all: true)
    def localHead = localGrgit.head()
    when:
    localGrgit.pull()
    then:
    localGrgit.status() == new Status(conflicts: ['1.txt'])
    localGrgit.head() == localHead
    thrown(IllegalStateException)
  }

  def 'pull to local repo with clean changes and rebase rebases changes on top of origin'() {
    given:
    repoFile(localGrgit, '3.txt') << '3.1\n'
    localGrgit.add(patterns: ['.'])
    localGrgit.commit(message: '3.1')
    def localHead = localGrgit.head()
    def remoteHead = remoteGrgit.resolve.toCommit('master')
    def localCommits = localGrgit.log {
      includes = [localHead.id]
      excludes = [ancestorHead.id]
    }
    when:
    localGrgit.pull(rebase: true)
    then:
    // includes all commits from remote
    localGrgit.log {
      includes = [remoteHead.id]
      excludes = ['HEAD']
    }.size() == 0

    // includes none of local commits
    localGrgit.log {
      includes = [localHead.id]
      excludes = ['HEAD']
    } == localCommits

    // has commit comments from local
    localGrgit.log {
      includes = ['HEAD']
      excludes = [remoteHead.id]
    }.collect {
      it.fullMessage
    } == localCommits.collect {
      it.fullMessage
    }

    // has state of all changes
    repoFile(localGrgit, '1.txt').text.normalize() == '1.1\n1.2\n1.3\n'
    repoFile(localGrgit, '3.txt').text.normalize() == '3.1\n'
  }

  def 'pull to local repo from other remote fast-forwards current branch'() {
    given:
    def otherRemoteUri = otherRemoteGrgit.repository.rootDir.toURI().toString()
    localGrgit.remote.add(name: 'other-remote', url: otherRemoteUri)
    when:
    localGrgit.pull(remote: 'other-remote')
    then:
    localGrgit.head() == otherRemoteGrgit.head()
  }

  def 'pull to local repo from specific remote branch merges changes'() {
    given:

    when:
    localGrgit.pull(branch: 'test-branch')
    then:
    (remoteGrgit.log(includes: ['test-branch']) - localGrgit.log()).size() == 0
  }
}
