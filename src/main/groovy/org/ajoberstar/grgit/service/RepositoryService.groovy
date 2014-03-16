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
package org.ajoberstar.grgit.service

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.operation.AddOp
import org.ajoberstar.grgit.operation.ApplyOp
import org.ajoberstar.grgit.operation.CheckoutOp
import org.ajoberstar.grgit.operation.CommitOp
import org.ajoberstar.grgit.operation.FetchOp
import org.ajoberstar.grgit.operation.LogOp
import org.ajoberstar.grgit.operation.PushOp
import org.ajoberstar.grgit.operation.RmOp
import org.ajoberstar.grgit.operation.ResetOp
import org.ajoberstar.grgit.operation.RevertOp
import org.ajoberstar.grgit.operation.StatusOp
import org.ajoberstar.grgit.util.JGitUtil
import org.ajoberstar.grgit.util.OpSyntaxUtil

/**
 *
 */
class RepositoryService {
	private static final Map OPERATIONS = [
		status: StatusOp, add: AddOp, remove: RmOp, reset: ResetOp, apply: ApplyOp,
		/*pull: PullOp,*/ push: PushOp, fetch: FetchOp,
		checkout: CheckoutOp,
		log: LogOp, commit: CommitOp, revert: RevertOp/*,
		cherryPick: CherryPickOp, merge: MergeOp, rebase: RebaseOp*/].asImmutable()

	final Repository repository

	final BranchService branch
	final NoteService note
	final RemoteService remote
	final StashService stash
	final TagService tag

	RepositoryService(Repository repository) {
		this.repository = repository
		this.branch = new BranchService(repository)
		this.note = null
		this.remote = null
		this.stash = null
		this.tag = null
	}

	Commit head() {
		return resolveCommit('HEAD')
	}

	Commit resolveCommit(String revstr) {
		return JGitUtil.resolveCommit(repository, revstr)
	}

	def methodMissing(String name, args) {
		OpSyntaxUtil.tryOp(this.class, OPERATIONS, [repository] as Object[], name, args)
	}
}
