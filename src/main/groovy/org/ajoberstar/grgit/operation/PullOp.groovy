package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.api.PullCommand
import org.eclipse.jgit.api.PullResult
import org.eclipse.jgit.api.errors.GitAPIException

class PullOp implements Callable<Void> {
	private final Repository repo

	boolean rebase = false

	PullOp(Repository repo) {
		this.repo = repo
	}

	Void call() {
		PullCommand cmd = repo.git.pull()
		cmd.rebase = rebase

		try {
			PullResult result = cmd.call()
			println result
			if (!result.successful) {
				throw new GrgitException("Could not pull: ${result}")
			}
			return null
		} catch (GitAPIException e) {
			throw new GrgitException('Problem merging.', e)
		}
	}
}
