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

import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec

class CleanOpSpec extends SimpleGitOpSpec {
  def setup() {
    repoFile('.gitignore') << 'build/\n.project'
    repoFile('1.txt') << '.'
    repoFile('2.txt') << '.'
    repoFile('3.txt') << '.'
    repoFile('dir1/4.txt') << '.'
    repoFile('dir1/5.txt') << '.'
    repoFile('dir1/6.txt') << '.'
    repoFile('dir2/7.txt') << '.'
    repoFile('dir2/8.txt') << '.'
    repoDir('dir1/dir3')
    repoDir('dir2/dir4')
    repoDir('dir5')
    repoFile('build/8.txt') << '.'
    repoFile('.project') << '.'

    grgit.add(patterns: ['.gitignore', '1.txt', '2.txt', 'dir1', 'dir2/8.txt'])
    grgit.commit(message: 'do')
  }

  def 'clean with defaults deletes untracked files only'() {
    given:
    def expected = ['3.txt', 'dir2/7.txt'] as Set
    expect:
    grgit.clean() == expected
    expected.every { !repoFile(it).exists() }
  }

  def 'clean with paths only deletes from paths'() {
    given:
    def expected = ['dir2/7.txt'] as Set
    expect:
    grgit.clean(paths: ['dir2/7.txt']) == expected
    expected.every { !repoFile(it).exists() }
  }

  def 'clean with directories true also deletes untracked directories'() {
    given:
    def expected = ['3.txt', 'dir2/7.txt', 'dir5/', 'dir2/dir4/', 'dir1/dir3/'] as Set
    expect:
    grgit.clean(directories: true) == expected
    expected.every { !repoFile(it).exists() }
  }

  def 'clean with ignore false also deletes files ignored by .gitignore'() {
    given:
    def expected = ['3.txt', 'dir2/7.txt', '.project'] as Set
    expect:
    grgit.clean(ignore: false) == expected
    expected.every { !repoFile(it).exists() }
  }

  def 'clean with dry run true returns expected but does not delete them'() {
    given:
    def expected = ['3.txt', 'dir2/7.txt'] as Set
    expect:
    grgit.clean(dryRun: true) == expected
    expected.every { repoFile(it).exists() }
  }
}
