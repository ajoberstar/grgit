package org.ajoberstar.grgit.fixtures

import spock.lang.Specification

class GitOpSpec extends Specification {
	RepositoryService grgit

	protected File repoFile(String path, boolean makeDirs = true) {
		return GitTestUtil.repoFile(grgit, path, makeDirs)
	}
}
