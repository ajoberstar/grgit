package org.ajoberstar.grgit.operation

import spock.lang.Unroll

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Grgit
import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.fixtures.GitTestUtil
import org.ajoberstar.grgit.fixtures.MultiGitOpSpec
import org.ajoberstar.grgit.service.RepositoryService
import org.ajoberstar.grgit.util.JGitUtil

class PullOpSpec extends MultiGitOpSpec {
	RepositoryService localGrgit
	RepositoryService remoteGrgit
	Commit ancestorHead

	def setup() {
		remoteGrgit = init('remote')

		repoFile(remoteGrgit, '1.txt') << '1.1\n'
		remoteGrgit.add(patterns: ['.'])
		ancestorHead = remoteGrgit.commit(message: '1.1', all: true)

		remoteGrgit.branch.add(name: 'test-branch')

		localGrgit = clone('local', remoteGrgit)
		localGrgit.branch.add(name: 'test-branch', startPoint: 'origin/test-branch')

		repoFile(remoteGrgit, '1.txt') << '1.2\n'
		remoteGrgit.commit(message: '1.2', all: true)
		repoFile(remoteGrgit, '1.txt') << '1.3\n'
		remoteGrgit.commit(message: '1.3', all: true)

		remoteGrgit.checkout(branch: 'test-branch')

		repoFile(remoteGrgit, '2.txt') << '2.1\n'
		remoteGrgit.add(patterns: ['.'])
		remoteGrgit.commit(message: '2.1', all: true)
		repoFile(remoteGrgit, '2.txt') << '2.2\n'
		remoteGrgit.commit(message: '2.2', all: true)
	}

	def 'pull to local repo with no changes fast-forwards current branch only'() {
		given:
		def localTestBranchHead = localGrgit.resolveCommit('test-branch')
		when:
		localGrgit.pull()
		then:
		localGrgit.head() == remoteGrgit.resolveCommit('master')
		localGrgit.resolveCommit('test-branch') == localTestBranchHead
	}

	def 'pull to local repo with clean changes merges branches from origin'() {
		given:
		repoFile(localGrgit, '3.txt') << '3.1\n'
		localGrgit.add(patterns: ['.'])
		localGrgit.commit(message: '3.1')
		def localHead = localGrgit.head()
		def remoteHead = remoteGrgit.resolveCommit('master')
		when:
		localGrgit.pull()
		then:
		// includes all commits from remote
		localGrgit.log {
			includes = [remoteHead.id]
			excludes = ['HEAD']
		}.size() == 0

		// has merge commit
		localGrgit.log {
			includes = ['HEAD']
			excludes = [localHead.id, remoteHead.id]
		}.size() == 1
	}

	def 'pull to local repo with conflicting changes fails'() {
		given:
		repoFile(localGrgit, '1.txt') << '1.4\n'
		localGrgit.commit(message: '1.4', all: true)
		def localHead = localGrgit.head()
		when:
		localGrgit.pull()
		then:
		localGrgit.status().clean
		localGrgit.head() == localHead
		thrown(GrgitException)
	}

	def 'pull to local repo with clean changes and rebase rebases changes on top of origin'() {
		given:
		repoFile(localGrgit, '3.txt') << '3.1\n'
		localGrgit.add(patterns: ['.'])
		localGrgit.commit(message: '3.1')
		def localHead = localGrgit.head()
		def remoteHead = remoteGrgit.resolveCommit('master')
		def localCommits = localGrgit.log {
			includes = [localHead.id]
			excludes = [ancestorHead.id]
		}
		when:
		localGrgit.pull(rebase: true)
		then:
		// includes all commits from remote
		localGrgit.log {
			includes = [remoteHead.id]
			excludes = ['HEAD']
		}.size() == 0

		// includes none of local commits
		localGrgit.log {
			includes = [localHead.id]
			excludes = ['HEAD']
		} == localCommits

		// has commit comments from local
		localGrgit.log {
			includes = ['HEAD']
			excludes = [remoteHead.id]
		}.collect {
			it.fullMessage
		} == localCommits.collect {
			it.fullMessage
		}

		// has state of all changes
		repoFile(localGrgit, '1.txt').text == String.format('1.1%n1.2%n1.3%n')
		repoFile(localGrgit, '3.txt').text == String.format('3.1%n')
	}
}
