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

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Remote
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.util.JGitUtil
import org.eclipse.jgit.lib.Config
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.transport.RemoteConfig
import org.eclipse.jgit.transport.URIish

/**
 * Adds a remote to the repository. Returns the newly created {@link org.ajoberstar.grgit.Remote}.
 * If remote with given name already exists, this command will fail.
 * @see <a href="http://ajoberstar.org/grgit/grgit-remote.html">grgit-remote</a>
 * @see <a href="http://git-scm.com/docs/git-remote">git-remote Manual Page</a>
 */
@Operation('add')
class RemoteAddOp implements Callable<Remote> {

  private final Repository repository

  /**
   * Name of the remote.
   */
  String name

  /**
   * URL to fetch from.
   */
  String url

  /**
   * URL to push to.
   */
  String pushUrl

  /**
   * Specs to fetch from the remote.
   */
  List fetchRefSpecs = []

  /**
   * Specs to push to the remote.
   */
  List pushRefSpecs = []

  /**
   * Whether or not pushes will mirror the repository.
   */
  boolean mirror

  RemoteAddOp(Repository repo) {
    this.repository = repo
  }

  @Override
  Remote call() {
    Config config = repository.jgit.repository.config
    if (RemoteConfig.getAllRemoteConfigs(config).find { it.name == name }) {
      throw new IllegalStateException("Remote $name already exists.")
    }
    def toUri = { url -> new URIish(url) }
    def toRefSpec = { spec -> new RefSpec(spec) }
    RemoteConfig remote = new RemoteConfig(config, name)
    if (url) { remote.addURI(toUri(url)) }
    if (pushUrl) { remote.addPushURI(toUri(pushUrl)) }
    remote.fetchRefSpecs = (fetchRefSpecs ?: ["+refs/heads/*:refs/remotes/$name/*"]).collect(toRefSpec)
    remote.pushRefSpecs = pushRefSpecs.collect(toRefSpec)
    remote.mirror = mirror
    remote.update(config)
    config.save()
    return JGitUtil.convertRemote(remote)
  }
}
