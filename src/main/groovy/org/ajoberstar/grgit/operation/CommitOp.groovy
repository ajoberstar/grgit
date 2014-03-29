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

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Person
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.api.CommitCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.revwalk.RevCommit

class CommitOp implements Callable<Commit> {
	private final Repository repo

	String message
	String reflogComment
	Person committer
	Person author
	Set<String> paths = []
	boolean all = false
	boolean amend = false

	CommitOp(Repository repo) {
		this.repo = repo
	}

	Commit call() {
		CommitCommand cmd = repo.jgit.commit()
		cmd.message = message
		cmd.reflogComment = reflogComment
		if (committer) { cmd.committer = new PersonIdent(committer.name, committer.email) }
		if (author) { cmd.author = new PersonIdent(author.name, author.email) }
		paths.each { cmd.setOnly(it) }
		if (all) { cmd.all = all }
		cmd.amend = amend
		try {
			RevCommit commit = cmd.call()
			return JGitUtil.convertCommit(commit)
		} catch (GitAPIException e) {
			throw new GrgitException('Problem committing changes.', e)
		}
	}
}
