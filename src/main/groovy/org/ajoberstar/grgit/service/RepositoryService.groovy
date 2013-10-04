/*
 * Copyright 2012-2013 the original author or authors.
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
package org.ajoberstar.grgit.service

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.operation.AddOp
import org.ajoberstar.grgit.operation.ApplyOp
import org.ajoberstar.grgit.operation.CommitOp
import org.ajoberstar.grgit.operation.LogOp
import org.ajoberstar.grgit.operation.RmOp
import org.ajoberstar.grgit.operation.ResetOp
import org.ajoberstar.grgit.operation.RevertOp
import org.ajoberstar.grgit.operation.StatusOp
import org.ajoberstar.grgit.util.ConfigureUtil
import org.ajoberstar.grgit.util.JGitUtil

/**
 *
 * @since 0.7.0
 * @author Andrew Oberstar
 */
class RepositoryService {
	private static final Map COMMANDS = [
		status: StatusOp, add: AddOp, remove: RmOp, reset: ResetOp, apply: ApplyOp,
		/*pull: PullOp, push: PushOp, fetch: FetchOp,
		checkout: CheckoutOp,*/
		log: LogOp, commit: CommitOp, revert: RevertOp/*,
		cherryPick: CherryPickOp, merge: MergeOp, rebase: RebaseOp*/].asImmutable()

	final Repository repository

	final BranchService branches
	final NoteService notes
	final RemoteService remotes
	final StashService stashes
	final TagService tags

	RepositoryService(Repository repository) {
		this.repository = repository
		this.branches = null
		this.notes = null
		this.remotes = null
		this.stashes = null
		this.tags = null
	}

	Commit head() {
		return JGitUtil.resolveCommit(repository, 'HEAD')
	}

	def methodMissing(String name, args) {
		if (name in COMMANDS && args.size() < 2) {
			def op = COMMANDS[name].newInstance(repository)
			def config = args.size() == 0 ? [:] : args[0]
			ConfigureUtil.configure(op, config)
			op.call()
		} else {
			throw new MissingMethodException(name, this.class, args)
		}
	}

	boolean isBare() {

	}

	void clean(Map parms) {

	}

	void garbageCollect(Map parms) {

	}
}
