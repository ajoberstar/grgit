/*
 * Copyright 2012-2014 the original author or authors.
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

import org.ajoberstar.grgit.Remote
import org.ajoberstar.grgit.Repository
import org.eclipse.jgit.lib.Config
import org.eclipse.jgit.transport.RemoteConfig
import org.eclipse.jgit.transport.URIish

import java.util.concurrent.Callable

/**
 * Adds a remote to the repository. Returns the newly created {@link org.ajoberstar.grgit.Remote}.
 * If remote with given name already exists, its URIs are replaced with newly specified ones.
 *
 * <p>To add remote.</p>
 *
 * <pre>
 * grgit.remote.add(name: 'new-remote', uri: 'git@github.com:username/project.git')
 * </pre>
 *
 * See <a href="http://git-scm.com/docs/git-remote">git-remote Manual Page</a>.
 *
 * @see <a href="http://git-scm.com/docs/git-remote">git-remote Manual Page</a>
 */
class RemoteAddOp implements Callable<Remote> {

    private final Repository repository

    /**
     * Name of origin.
     */
    private String name

    /**
     * URI pointing to origin.
     */
    private String uri

    RemoteAddOp(Repository repo) {
        this.repository = repo
    }

    @Override
    Remote call() {
        Config config = repository.jgit.repository.config

        RemoteConfig remote = new RemoteConfig(config, name)
        List<URIish> pushUris = new ArrayList<>(remote.pushURIs)
        for (URIish uri : pushUris) {
            remote.removePushURI(uri)
        }

        remote.addPushURI(new URIish(uri))
        remote.addURI(new URIish(uri))
        remote.update(config)

        config.save()

        return new Remote(name: name, uri: uri)
    }
}

