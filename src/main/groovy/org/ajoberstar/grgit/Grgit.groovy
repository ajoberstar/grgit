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
package org.ajoberstar.grgit

import org.ajoberstar.grgit.operation.CloneOp
import org.ajoberstar.grgit.operation.InitOp
import org.ajoberstar.grgit.service.RepositoryService
import org.ajoberstar.grgit.util.OpSyntaxUtil

import org.eclipse.jgit.api.Git

class Grgit {
	private static final Map OPERATIONS = [init: InitOp, clone: CloneOp]

	static {
		Grgit.metaClass.static.methodMissing = { name, args ->
			OpSyntaxUtil.tryOp(Grgit, OPERATIONS, [] as Object[], name, args)
		}
	}

	private Grgit() {
		throw new AssertionError('Cannot instantiate this class.')
	}

	static RepositoryService open(String rootDirPath) {
		return open(new File(rootDirPath))
	}

	static RepositoryService open(File rootDir) {
		def repo = new Repository(rootDir, Git.open(rootDir))
		return new RepositoryService(repo)
	}
}
