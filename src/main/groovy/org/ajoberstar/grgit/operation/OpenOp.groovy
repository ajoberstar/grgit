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
import org.ajoberstar.grgit.Credentials
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.util.CoercionUtil

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.InitCommand
import org.eclipse.jgit.api.errors.GitAPIException

/**
 * Opens an existing repository. Returns a {@link Grgit} pointing
 * to the resulting repository.
 *
 * <p>To open a repository in the current working directory.</p>
 *
 * <pre>
 * def grgit = Grgit.open(dir: '.')
 * </pre>
 *
 * <p>To open a repository using hard-coded credentials.</p>
 *
 * <pre>
 * def grgit = Grgit.open(dir: 'gradle-git', creds: new Credentials(username: 'user', password: 'pass'))
 * </pre>
 *
 * @since 1.0.0
 */
class OpenOp implements Callable<Grgit> {
	/**
	 * Hardcoded credentials to use for remote operations.
	 */
	Credentials creds

	/**
	 * The directory to open the repository from.
	 * @see {@link CoercionUtil#toFile(Object)}
	 */
	Object dir

	Grgit call() {
		def dirFile = CoercionUtil.toFile(dir)
		def repo = new Repository(dirFile, Git.open(dirFile), creds)
		return new Grgit(repo)
	}
}
