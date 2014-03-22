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

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.auth.TransportOpUtil
import org.ajoberstar.grgit.exception.GrgitException

import org.eclipse.jgit.api.FetchCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.transport.RefSpec

class FetchOp implements Callable<Void> {
	private final Repository repo

	String remote = 'origin'
	List refSpecs = []
	boolean prune = false
	TagMode tagMode = TagMode.AUTO

	FetchOp(Repository repo) {
		this.repo = repo
	}

	Void call() {
		FetchCommand cmd = repo.git.fetch()
		TransportOpUtil.configure(cmd, null)
		cmd.remote = remote
		cmd.refSpecs = refSpecs.collect { new RefSpec(it) }
		cmd.removeDeletedRefs = prune
		cmd.tagOpt = tagMode.jgit
		try {
			cmd.call()
			return null
		} catch (GitAPIException e) {
			throw new GrgitException('Problem fetching from remote.', e)
		}
	}
}
