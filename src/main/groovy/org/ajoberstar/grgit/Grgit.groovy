package org.ajoberstar.grgit

import org.ajoberstar.grgit.service.RepositoryService

import org.eclipse.jgit.api.Git

class Grgit {
	private Grgit() {
		throw new AssertionError('Cannot instantiate this class.')
	}

	static init() {

	}

	static clone() {

	}

	static RepositoryService open(File rootDir) {
		def repo = new Repository(rootDir, Git.open(rootDir))
		return new RepositoryService(repo)
	}
}
