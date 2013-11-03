package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.auth.TransportOpUtil
import org.ajoberstar.grgit.exception.GrGitException

import org.eclipse.jgit.api.FetchCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.transport.TagOpt

class FetchOp implements Callable<Void> {
	private Repository repo

	String remote = 'origin'
	List refSpecs = []
	boolean prune = false
	TagMode tagMode = TagMode.AUTO

	FetchOp(Repository repo) {
		this.repo = repo
	}

	Void call() {
		FetchCommand cmd = repo.git.fetch()
		TransportOpUtil.configure(cmd, null)
		cmd.remote = remote
		cmd.refSpecs = refSpecs.collect { new RefSpec(it) }
		cmd.removeDeletedRefs = prune
		cmd.tagOpt = tagMode.jgit
		try {
			cmd.call()
			return null
		} catch (GitAPIException e) {
			throw new GrGitException('Problem fetching from remote.', e)
		}
	}

	static enum TagMode {
		AUTO(TagOpt.AUTO_FOLLOW),
		ALL(TagOpt.FETCH_TAGS),
		NONE(TagOpt.NO_TAGS)

		protected final TagOpt jgit

		private TagMode(TagOpt opt) {
			this.jgit = opt
		}
	}
}
