package org.ajoberstar.grgit.fixtures

import spock.lang.Specification

import org.ajoberstar.grgit.service.RepositoryService
import org.ajoberstar.grgit.Grgit

import org.eclipse.jgit.api.Git

import org.junit.Rule
import org.junit.rules.TemporaryFolder

class MultiGitOpSpec extends Specification {
	@Rule TemporaryFolder tempDir = new TemporaryFolder()

	protected RepositoryService init(String name) {
		File repoDir = tempDir.newFolder(name)
		Git git = Git.init().setDirectory(repoDir).call()
		return Grgit.open(repoDir)
	}

	protected File repoFile(RepositoryService grgit, String path, boolean makeDirs = true) {
		return GitTestUtil.repoFile(grgit, path, makeDirs)
	}
}
