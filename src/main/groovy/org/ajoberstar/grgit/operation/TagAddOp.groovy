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

import org.ajoberstar.grgit.Person
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.Tag
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.api.TagCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.lib.Ref

class TagAddOp implements Callable<Tag> {
	private final Repository repo

	String name
	String message
	Person tagger
	boolean annotate = true
	// boolean sign = false
	boolean force = false

	String pointsTo

	TagAddOp(Repository repo) {
		this.repo = repo
	}

	Tag call() {
		TagCommand cmd = repo.jgit.tag()
		cmd.name = name
		cmd.message = message
		if (tagger) { cmd.tagger = new PersonIdent(tagger.name, tagger.email) }
		cmd.annotated = annotate
		// cmd.signed = sign
		cmd.forceUpdate = force
		if (pointsTo) { cmd.objectId = JGitUtil.resolveRevObject(repo, pointsTo) }

		try {
			Ref ref = cmd.call()
			return JGitUtil.resolveTag(repo, ref)
		} catch (GitAPIException e) {
			throw new GrgitException('Problem creating tag.', e)
		}
	}
}
