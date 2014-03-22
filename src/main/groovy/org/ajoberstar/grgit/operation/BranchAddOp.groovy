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

import org.ajoberstar.grgit.Branch
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.exception.GrgitException

import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.Ref

class BranchAddOp implements Callable<Branch> {
	private final Repository repo

	String name
	boolean force = false
	String startPoint
	// Mode mode

	BranchAddOp(Repository repo) {
		this.repo = repo
	}

	Branch call() {
		CreateBranchCommand cmd = repo.git.branchCreate()
		cmd.name = name
		cmd.force = force
		if (startPoint) { cmd.startPoint = startPoint }
		// cmd.upstreamMode = mode.jgit

		try {
			Ref ref = cmd.call()
			return new Branch(name)
		} catch (GitAPIException e) {
			throw new GrgitException('Problem creating branch.', e)
		}
	}

	// static enum Mode {
	// 	NO_TRACK(CreateBranchCommand.SetupUpstreamMode.NOTRACK),
	// 	TRACK(CreateBranchCommand.SetupUpstreamMode.TRACK),
	// 	SET_UPSTREAM(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)

	// 	protected final CreateBranchCommand.SetupUpstreamMode jgit

	// 	private Mode(CreateBranchCommand.SetupUpstreamMode jgit) {
	// 		this.jgit = jgit
	// 	}
	// }
}
