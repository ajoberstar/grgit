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
import org.ajoberstar.grgit.Ref
import org.ajoberstar.grgit.fixtures.MultiGitOpSpec
import org.eclipse.jgit.api.errors.GitAPIException

import spock.lang.Unroll

class LsRemoteOpSpec extends MultiGitOpSpec {
  Grgit localGrgit
  Grgit remoteGrgit

  List branches = []
  List tags = []

  def setup() {
    remoteGrgit = init('remote')

    branches << remoteGrgit.branch.current

    repoFile(remoteGrgit, '1.txt') << '1'
    remoteGrgit.commit(message: 'do', all: true)

    branches << remoteGrgit.branch.add(name: 'my-branch')

    localGrgit = clone('local', remoteGrgit)

    repoFile(remoteGrgit, '1.txt') << '2'
    remoteGrgit.commit(message: 'do', all: true)

    tags << remoteGrgit.tag.add(name: 'reachable-tag')
    branches << remoteGrgit.branch.add(name: 'sub/mine1')

    remoteGrgit.checkout {
      branch = 'unreachable-branch'
      createBranch = true
    }
    branches << remoteGrgit.branch.list().find { it.name == 'unreachable-branch' }

    repoFile(remoteGrgit, '1.txt') << '2.5'
    remoteGrgit.commit(message: 'do-unreachable', all: true)

    tags << remoteGrgit.tag.add(name: 'unreachable-tag')

    remoteGrgit.checkout(branch: 'master')

    repoFile(remoteGrgit, '1.txt') << '3'
    remoteGrgit.commit(message: 'do', all: true)

    branches << remoteGrgit.branch.add(name: 'sub/mine2')

    println remoteGrgit.branch.list()
    println remoteGrgit.tag.list()
  }

  def 'lsremote from non-existent remote fails'() {
    when:
    localGrgit.lsremote(remote: 'fake')
    then:
    thrown(GitAPIException)
  }

  def 'lsremote returns all refs'() {
    expect:
    localGrgit.lsremote() == format([new Ref('HEAD'), branches, tags].flatten())
  }

  def 'lsremote returns branches and tags'() {
    expect:
    localGrgit.lsremote(heads: true, tags: true) == format([branches, tags].flatten())
  }

  def 'lsremote returns only branches'() {
    expect:
    localGrgit.lsremote(heads: true) == format(branches)
  }

  def 'lsremote returns only tags'() {
    expect:
    localGrgit.lsremote(tags: true) == format(tags)
  }

  private Map format(things) {
    return things.collectEntries { refish ->
      Ref ref = new Ref(refish.fullName)
      [(ref): remoteGrgit.resolve.toObjectId(refish)]
    }
  }
}
