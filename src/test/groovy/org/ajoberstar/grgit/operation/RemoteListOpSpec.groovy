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
import org.ajoberstar.grgit.Remote
import org.ajoberstar.grgit.fixtures.MultiGitOpSpec

class RemoteListOpSpec extends MultiGitOpSpec {
  def 'will list all remotes'() {
    given:
    Grgit remoteGrgit = init('remote')

    repoFile(remoteGrgit, '1.txt') << '1'
    remoteGrgit.commit(message: 'do', all: true)

    Grgit localGrgit = clone('local', remoteGrgit)

    expect:
    localGrgit.remote.list() == [
      new Remote(
        name: 'origin',
        url: remoteGrgit.repository.rootDir.canonicalFile.toPath().toUri(),
        fetchRefSpecs: ['+refs/heads/*:refs/remotes/origin/*'])
    ]
  }
}
