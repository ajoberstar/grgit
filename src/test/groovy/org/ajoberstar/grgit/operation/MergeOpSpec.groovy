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

import static org.ajoberstar.grgit.operation.MergeOp.Mode.*

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.fixtures.MultiGitOpSpec
import org.eclipse.jgit.api.errors.GitAPIException

import spock.lang.Unroll

class MergeOpSpec extends MultiGitOpSpec {
  Grgit localGrgit
  Grgit remoteGrgit

  def setup() {
    remoteGrgit = init('remote')

    repoFile(remoteGrgit, '1.txt') << '1.1\n'
    remoteGrgit.add(patterns: ['.'])
    remoteGrgit.commit(message: '1.1', all: true)
    repoFile(remoteGrgit, '2.txt') << '2.1\n'
    remoteGrgit.add(patterns: ['.'])
    remoteGrgit.commit(message: '2.1', all: true)

    localGrgit = clone('local', remoteGrgit)

    remoteGrgit.checkout(branch: 'ff', createBranch: true)

    repoFile(remoteGrgit, '1.txt') << '1.2\n'
    remoteGrgit.commit(message: '1.2', all: true)
    repoFile(remoteGrgit, '1.txt') << '1.3\n'
    remoteGrgit.commit(message: '1.3', all: true)

    remoteGrgit.checkout(branch: 'clean', startPoint: 'master', createBranch: true)

    repoFile(remoteGrgit, '3.txt') << '3.1\n'
    remoteGrgit.add(patterns: ['.'])
    remoteGrgit.commit(message: '3.1', all: true)
    repoFile(remoteGrgit, '3.txt') << '3.2\n'
    remoteGrgit.commit(message: '3.2', all: true)

    remoteGrgit.checkout(branch: 'conflict', startPoint: 'master', createBranch: true)

    repoFile(remoteGrgit, '2.txt') << '2.2\n'
    remoteGrgit.commit(message: '2.2', all: true)
    repoFile(remoteGrgit, '2.txt') << '2.3\n'
    remoteGrgit.commit(message: '2.3', all: true)

    localGrgit.checkout(branch: 'merge-test', createBranch: true)

    repoFile(localGrgit, '2.txt') << '2.a\n'
    localGrgit.commit(message: '2.a', all: true)
    repoFile(localGrgit, '2.txt') << '2.b\n'
    localGrgit.commit(message: '2.b', all: true)

    localGrgit.fetch()
  }

  @Unroll('merging #head with #mode does a fast-forward merge')
  def 'fast-forward merge happens when expected'() {
    given:
    localGrgit.checkout(branch: 'master')
    when:
    localGrgit.merge(head: head, mode: mode)
    then:
    localGrgit.status().clean
    localGrgit.head() == remoteGrgit.resolve.toCommit(head - 'origin/')
    where:
    head		| mode
    'origin/ff' | DEFAULT
    'origin/ff' | ONLY_FF
    'origin/ff' | NO_COMMIT
  }

  @Unroll('merging #head with #mode creates a merge commit')
  def 'merge commits created when expected'() {
    given:
    def oldHead = localGrgit.head()
    def mergeHead = remoteGrgit.resolve.toCommit(head - 'origin/')
    when:
    localGrgit.merge(head: head, mode: mode)
    then:
    localGrgit.status().clean

    // has a merge commit
    localGrgit.log {
      includes = ['HEAD']
      excludes = [oldHead.id, mergeHead.id]
    }.size() == 1
    where:
    head		   | mode
    'origin/ff'	| CREATE_COMMIT
    'origin/clean' | DEFAULT
    'origin/clean' | CREATE_COMMIT
  }

  @Unroll('merging #head with #mode merges but leaves them uncommitted')
  def 'merge left uncommitted when expected'() {
    given:
    def oldHead = localGrgit.head()
    def mergeHead = remoteGrgit.resolve.toCommit(head - 'origin/')
    when:
    localGrgit.merge(head: head, mode: mode)
    then:
    localGrgit.status() == status
    localGrgit.head() == oldHead
    repoFile(localGrgit, '.git/MERGE_HEAD').text.trim() == mergeHead.id
    where:
    head		   | mode	  | status
    'origin/clean' | NO_COMMIT | new Status(staged: [added: ['3.txt']])
  }

  @Unroll('merging #head with #mode squashes changes but leaves them uncommitted')
  def 'squash merge happens when expected'() {
    given:
    def oldHead = localGrgit.head()
    when:
    localGrgit.merge(head: head, mode: mode)
    then:
    localGrgit.status() == status
    localGrgit.head() == oldHead
    !repoFile(localGrgit, '.git/MERGE_HEAD').exists()
    where:
    head		   | mode   | status
    'origin/ff'	| SQUASH | new Status(staged: [modified: ['1.txt']])
    'origin/clean' | SQUASH | new Status(staged: [added: ['3.txt']])
  }

  @Unroll('merging #head with #mode fails with correct status')
  def 'merge fails as expected'() {
    given:
    def oldHead = localGrgit.head()
    when:
    localGrgit.merge(head: head, mode: mode)
    then:
    localGrgit.head() == oldHead
    localGrgit.status() == status
    thrown(IllegalStateException)
    where:
    head			  | mode		  | status
    'origin/clean'	| ONLY_FF	   | new Status()
    'origin/conflict' | DEFAULT	   | new Status(conflicts: ['2.txt'])
    'origin/conflict' | ONLY_FF	   | new Status()
    'origin/conflict' | CREATE_COMMIT | new Status(conflicts: ['2.txt'])
    'origin/conflict' | SQUASH		| new Status(conflicts: ['2.txt'])
    'origin/conflict' | NO_COMMIT	 | new Status(conflicts: ['2.txt'])
  }

  def 'merge uses message if supplied'() {
    given:
    def oldHead = localGrgit.head()
    def mergeHead = remoteGrgit.resolve.toCommit('clean')
    when:
    localGrgit.merge(head: 'origin/clean', message: 'Custom message')
    then: 'all changes are committed'
    localGrgit.status().clean
    and: 'a merge commit was created'
    localGrgit.log {
      includes = ['HEAD']
      excludes = [oldHead.id, mergeHead.id]
    }.size() == 1
    and: 'the merge commits message is what was passed in'
    localGrgit.head().shortMessage == 'Custom message'
  }

  def 'merge of a branch includes this in default message'() {
    given:
    def oldHead = localGrgit.head()
    def mergeHead = remoteGrgit.resolve.toCommit('clean')
    when:
    localGrgit.merge(head: 'origin/clean')
    then: 'all changes are committed'
    localGrgit.status().clean
    and: 'a merge commit was created'
    localGrgit.log {
      includes = ['HEAD']
      excludes = [oldHead.id, mergeHead.id]
    }.size() == 1
    and: 'the merge commits message mentions branch name'
    localGrgit.head().shortMessage == 'Merge remote-tracking branch \'origin/clean\' into merge-test'
  }

  def 'merge of a commit includes this in default message'() {
    given:
    def oldHead = localGrgit.head()
    def mergeHead = remoteGrgit.resolve.toCommit('clean')
    when:
    localGrgit.merge(head: mergeHead.id)
    then: 'all changes are committed'
    localGrgit.status().clean
    and: 'a merge commit was created'
    localGrgit.log {
      includes = ['HEAD']
      excludes = [oldHead.id, mergeHead.id]
    }.size() == 1
    and: 'the merge commits message mentions commit hash'
    localGrgit.head().shortMessage == "Merge commit '${mergeHead.id}' into merge-test"
  }
}
