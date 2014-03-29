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
import org.ajoberstar.grgit.exception.GrgitException

import org.eclipse.jgit.api.CleanCommand
import org.eclipse.jgit.api.errors.GitAPIException

class CleanOp implements Callable<Set<String>> {
	private final Repository repo

	Set<String> paths
	boolean directories = false
	boolean dryRun = false
	boolean ignore = true

	CleanOp(Repository repo) {
		this.repo = repo
	}

	Set<String> call() {
		CleanCommand cmd = repo.jgit.clean()
		if (paths) { cmd.paths = paths }
		cmd.cleanDirectories = directories
		cmd.dryRun = dryRun
		cmd.ignore = ignore

		try {
			return cmd.call()
		} catch (GitAPIException e) {
			throw new GrgitException('Problem cleaning repository.', e)
		}
	}
}
