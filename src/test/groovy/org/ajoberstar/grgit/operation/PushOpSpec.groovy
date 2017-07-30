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

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.fixtures.GitTestUtil
import org.ajoberstar.grgit.fixtures.MultiGitOpSpec
import org.eclipse.jgit.api.errors.GitAPIException

class PushOpSpec extends MultiGitOpSpec {
  Grgit localGrgit
  Grgit remoteGrgit

  def setup() {
    // TODO: Conver to Grgit after branch and tag

    remoteGrgit = init('remote')

    repoFile(remoteGrgit, '1.txt') << '1'
    remoteGrgit.commit(message: 'do', all: true)

    remoteGrgit.branch.add(name: 'my-branch')

    localGrgit = clone('local', remoteGrgit)
    localGrgit.checkout(branch: 'my-branch', createBranch: true)

    repoFile(localGrgit, '1.txt') << '1.5'
    localGrgit.commit(message: 'do', all: true)

    localGrgit.tag.add(name: 'tag1')

    localGrgit.checkout(branch: 'master')

    repoFile(localGrgit, '1.txt') << '2'
    localGrgit.commit(message: 'do', all: true)

    localGrgit.tag.add(name: 'tag2')
  }

  def 'push to non-existent remote fails'() {
    when:
    localGrgit.push(remote: 'fake')
    then:
    thrown(GitAPIException)
  }

  def 'push without other settings pushes correct commits'() {
    when:
    localGrgit.push()
    then:
    GitTestUtil.resolve(localGrgit, 'refs/heads/master') == GitTestUtil.resolve(remoteGrgit, 'refs/heads/master')
    GitTestUtil.resolve(localGrgit, 'refs/heads/my-branch') != GitTestUtil.resolve(remoteGrgit, 'refs/heads/my-branch')
    !GitTestUtil.tags(remoteGrgit)
  }

  def 'push with all true pushes all branches'() {
    when:
    localGrgit.push(all: true)
    then:
    GitTestUtil.resolve(localGrgit, 'refs/heads/master') == GitTestUtil.resolve(remoteGrgit, 'refs/heads/master')
    GitTestUtil.resolve(localGrgit, 'refs/heads/my-branch') == GitTestUtil.resolve(remoteGrgit, 'refs/heads/my-branch')
    !GitTestUtil.tags(remoteGrgit)
  }

  def 'push with tags true pushes all tags'() {
    when:
    localGrgit.push(tags: true)
    then:
    GitTestUtil.resolve(localGrgit, 'refs/heads/master') != GitTestUtil.resolve(remoteGrgit, 'refs/heads/master')
    GitTestUtil.resolve(localGrgit, 'refs/heads/my-branch') != GitTestUtil.resolve(remoteGrgit, 'refs/heads/my-branch')
    GitTestUtil.tags(localGrgit) == GitTestUtil.tags(remoteGrgit)
  }

  def 'push with refs only pushes those refs'() {
    when:
    localGrgit.push(refsOrSpecs: ['my-branch'])
    then:
    GitTestUtil.resolve(localGrgit, 'refs/heads/master') != GitTestUtil.resolve(remoteGrgit, 'refs/heads/master')
    GitTestUtil.resolve(localGrgit, 'refs/heads/my-branch') == GitTestUtil.resolve(remoteGrgit, 'refs/heads/my-branch')
    !GitTestUtil.tags(remoteGrgit)
  }

  def 'push with refSpecs only pushes those refs'() {
    when:
    localGrgit.push(refsOrSpecs: ['+refs/heads/my-branch:refs/heads/other-branch'])
    then:
    GitTestUtil.resolve(localGrgit, 'refs/heads/master') != GitTestUtil.resolve(remoteGrgit, 'refs/heads/master')
    GitTestUtil.resolve(localGrgit, 'refs/heads/my-branch') != GitTestUtil.resolve(remoteGrgit, 'refs/heads/my-branch')
    GitTestUtil.resolve(localGrgit, 'refs/heads/my-branch') == GitTestUtil.resolve(remoteGrgit, 'refs/heads/other-branch')
    !GitTestUtil.tags(remoteGrgit)
  }

  def 'push in dryRun mode does not push commits'() {
    given:
    def remoteMasterHead = GitTestUtil.resolve(remoteGrgit, 'refs/heads/master')
    when:
    localGrgit.push(dryRun: true)
    then:
    GitTestUtil.resolve(localGrgit, 'refs/heads/master') != GitTestUtil.resolve(remoteGrgit, 'refs/heads/master')
    GitTestUtil.resolve(remoteGrgit, 'refs/heads/master') == remoteMasterHead
  }

  def 'push in dryRun mode does not push tags'() {
    given:
    def remoteMasterHead = GitTestUtil.resolve(remoteGrgit, 'refs/heads/master')
    when:
    localGrgit.push(dryRun: true, tags: true)
    then:
    !GitTestUtil.tags(remoteGrgit)
  }
}
