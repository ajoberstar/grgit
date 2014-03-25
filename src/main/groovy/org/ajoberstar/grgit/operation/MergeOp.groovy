package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.util.JGitUtil

import org.eclipse.jgit.api.MergeCommand
import org.eclipse.jgit.api.MergeResult
import org.eclipse.jgit.api.errors.GitAPIException

class MergeOp implements Callable<Void> {
	private final Repository repo

	String head
	Mode mode

	MergeOp(Repository repo) {
		this.repo = repo
	}

	Void call() {
		MergeCommand cmd = repo.git.merge()
		if (head) { cmd.include(JGitUtil.resolveObject(repo, head)) }
		switch (mode) {
			case Mode.ONLY_FF:
				cmd.fastForward = MergeCommand.FastForwardMode.FF_ONLY
				break
			case Mode.CREATE_COMMIT:
				cmd.fastForward = MergeCommand.FastForwardMode.NO_FF
				break
			case Mode.SQUASH:
				cmd.squash = true
				break
			case Mode.NO_COMMIT:
				cmd.commit = false
				break
		}

		try {
			MergeResult result = cmd.call()
			if (!result.mergeStatus.successful) {
				throw new GrgitException("Could not merge: ${result}")
			}
			return null
		} catch (GitAPIException e) {
			throw new GrgitException('Problem merging.', e)
		}
	}

	static enum Mode {
		DEFAULT,
		ONLY_FF,
		CREATE_COMMIT,
		SQUASH,
		NO_COMMIT
	}
}
