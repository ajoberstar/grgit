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
import org.eclipse.jgit.transport.TagOpt

/**
 * Fetch changes from remotes.
 *
 * <p>To fetch changes from the {@code origin} remote.</p>
 *
 * <pre>
 * grgit.fetch()
 * </pre>
 *
 * <p>To remove branches locally that were removed from the remote.</p>
 *
 * <pre>
 * grgit.fetch(prune: true)
 * </pre>
 *
 * <p>To pull down all tags from the remote.</p>
 *
 * <pre>
 * grgit.fetch(tagMode: FetchOp.TagMode.ALL)
 * </pre>
 *
 * <p>To fetch without pulling down tags.</p>
 *
 * <pre>
 * grgit.fetch(tagMode: FetchOp.TagMode.NONE)
 * </pre>
 *
 * See <a href="http://git-scm.com/docs/git-fetch">git-fetch Manual Reference.</a>
 *
 * @since 0.2.0
 * @see <a href="http://git-scm.com/docs/git-fetch">git-fetch Manual Reference.</a>
 */
class FetchOp implements Callable<Void> {
	private final Repository repo

	/**
	 * Which remote should be fetched. Defaults to {@code origin}.
	 */
	String remote = 'origin'

	/**
	 * List of refspecs to fetch.
	 */
	List refSpecs = []

	/**
	 * {@code true} if branches removed by the remote should be
	 * removed locally.
	 */
	boolean prune = false

	/**
	 * How should tags be handled.
	 */
	TagMode tagMode = TagMode.AUTO

	FetchOp(Repository repo) {
		this.repo = repo
	}

	Void call() {
		FetchCommand cmd = repo.jgit.fetch()
		TransportOpUtil.configure(cmd, repo.credentials)
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

	enum TagMode {
		AUTO(TagOpt.AUTO_FOLLOW),
		ALL(TagOpt.FETCH_TAGS),
		NONE(TagOpt.NO_TAGS)

		final TagOpt jgit

		private TagMode(TagOpt opt) {
			this.jgit = opt
		}
	}
}
