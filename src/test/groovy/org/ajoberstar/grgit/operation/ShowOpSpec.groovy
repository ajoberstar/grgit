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
import org.ajoberstar.grgit.CommitDiff
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec

class ShowOpSpec extends SimpleGitOpSpec {

  def 'can show diffs in commit that added new file'() {
    File fooFile = repoFile("dir1/foo.txt")
    fooFile << "foo!"
    grgit.add(patterns: ['.'])
    Commit commit = grgit.commit(message: "Initial commit")

    expect:
    grgit.show(commit: commit) == new CommitDiff(
      commit: commit,
      added: ['dir1/foo.txt']
    )
  }

  def 'can show diffs in commit that modified existing file'() {
    File fooFile = repoFile("bar.txt")
    fooFile << "bar!"
    grgit.add(patterns: ['.'])
    grgit.commit(message: "Initial commit")

    // Change existing file
    fooFile << "monkey!"
    grgit.add(patterns: ['.'])
    Commit changeCommit = grgit.commit(message: "Added monkey")

    expect:
    grgit.show(commit: changeCommit) == new CommitDiff(
      commit: changeCommit,
      modified: ['bar.txt']
    )
  }

  def 'can show diffs in commit that deleted existing file'() {
    File fooFile = repoFile("bar.txt")
    fooFile << "bar!"
    grgit.add(patterns: ['.'])
    grgit.commit(message: "Initial commit")

    // Delete existing file
    grgit.remove(patterns: ['bar.txt'])
    Commit removeCommit = grgit.commit(message: "Deleted file")

    expect:
    grgit.show(commit: removeCommit) == new CommitDiff(
      commit: removeCommit,
      removed: ['bar.txt']
    )
  }

  def 'can show diffs in commit with multiple changes'() {
    File animalFile = repoFile("animals.txt")
    animalFile << "giraffe!"
    grgit.add(patterns: ['.'])
    grgit.commit(message: "Initial commit")

    // Change existing file
    animalFile << "zebra!"

    // Add new file
    File fishFile = repoFile("salmon.txt")
    fishFile<< "salmon!"
    grgit.add(patterns: ['.'])
    Commit changeCommit = grgit.commit(message: "Add fish and update animals with zebra")

    expect:
    grgit.show(commit: changeCommit) == new CommitDiff(
      commit: changeCommit,
      modified: ['animals.txt'],
      added: ['salmon.txt']
    )
  }

  def 'can show diffs in commit with rename'() {
    given:
    repoFile('elephant.txt') << 'I have tusks.'
    grgit.add(patterns: ['.'])
    grgit.commit(message: 'Adding elephant.')

    repoFile('elephant.txt').renameTo(repoFile('mammoth.txt'))
    grgit.add(patterns: ['.'])
    grgit.remove(patterns: ['elephant.txt'])
    Commit renameCommit = grgit.commit(message: 'Renaming to mammoth.')

    expect:
    grgit.show(commit: renameCommit) == new CommitDiff(
      commit: renameCommit,
      renamed: ['mammoth.txt']
    )
  }

  def 'can show diffs based on rev string'() {
    File fooFile = repoFile("foo.txt")
    fooFile << "foo!"
    grgit.add(patterns: ['.'])
    Commit commit = grgit.commit(message: "Initial commit")

    expect:
    grgit.show(commit: commit.id) == new CommitDiff(
      commit: commit,
      added: ['foo.txt']
    )
  }
}
