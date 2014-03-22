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

import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.service.RepositoryService

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.errors.GitAPIException

class CloneOp implements Callable<RepositoryService> {
	File dir
	String uri
	String remote = 'origin'
	boolean bare = false
	boolean checkout = true
	String refToCheckout

	RepositoryService call() {
		if (!checkout && refToCheckout) {
			throw new IllegalArgumentException('Cannot specify a refToCheckout and set checkout to false.')
		}

		CloneCommand cmd = Git.cloneRepository()
		cmd.directory = dir
		cmd.uri = uri
		cmd.remote = remote
		cmd.bare = bare
		cmd.noCheckout = !checkout
		if (refToCheckout) { cmd.branch = refToCheckout }

		try {
			cmd.call()
			return Grgit.open(dir)
		} catch (GitAPIException e) {
			throw new GrgitException('Problem cloning repository.', e)
		}
	}
}
