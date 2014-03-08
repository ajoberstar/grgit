package org.ajoberstar.grgit.fixtures

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.service.RepositoryService
import org.ajoberstar.grgit.util.JGitUtil
import org.eclipse.jgit.transport.RemoteConfig

final class GitTestUtil {
	private GitTestUtil() {
		throw new AssertionError('Cannot instantiate this class.')
	}

	static File repoFile(RepositoryService grgit, String path, boolean makeDirs = true) {
		def file = new File(grgit.repository.rootDir, path)
		if (makeDirs) file.parentFile.mkdirs()
		return file
	}

	static List branches(RepositoryService grgit) {
		return grgit.repository.git.branchList().with {
			listMode = ListMode.ALL
			delegate.call()
		}.collect { it.name }
	}

	static List tags(RepositoryService grgit) {
		return grgit.repository.git.tagList().call().collect { it.name }
	}

	static List remotes(RepositoryService grgit) {
		def jgitConfig = grgit.repository.git.repo.config
		return RemoteConfig.getAllRemoteConfigs(jgitConfig).collect { it.name}
	}

	static Commit head(RepositoryService grgit, String ref) {
		return JGitUtil.resolveCommit(grgit.repository, ref)
	}
}
