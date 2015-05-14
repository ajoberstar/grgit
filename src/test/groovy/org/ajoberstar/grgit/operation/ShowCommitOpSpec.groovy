/*
 * Copyright 2012-2015 the original author or authors.
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

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.CommitDiff
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec
import org.eclipse.jgit.diff.DiffEntry

class ShowCommitOpSpec extends SimpleGitOpSpec {

	def 'can show diffs in commit that added new file'() {
		File fooFile = repoFile("foo.txt")
		fooFile << "foo!"
		grgit.add(patterns: ['.'])
		Commit commit = grgit.commit(message: "Initial commit")

		when:
		CommitDiff diff = grgit.show(commit: commit)

		then:
		diff.commit == commit
		diff.diffs.size() == 1
		diff.diffs.first().fileName == "foo.txt"
		diff.diffs.first().changeType == DiffEntry.ChangeType.ADD
		diff.diffs.diffAsString
	}

	def 'can show diffs in commit that modified existing file'() {
		File fooFile = repoFile("bar.txt")
		fooFile << "bar!"
		grgit.add(patterns: ['.'])
		grgit.commit(message: "Initial commit")

		// Change existing file
		fooFile << "monkey!"
		grgit.add(patterns: ['.'])
		Commit changeCommit = grgit.commit(message: "Added monkey")

		when:
		CommitDiff diff = grgit.show(commit: changeCommit)

		then:
		diff.commit == changeCommit
		diff.diffs.size() == 1
		diff.diffs.first().fileName == "bar.txt"
		diff.diffs.first().changeType == DiffEntry.ChangeType.MODIFY
		diff.diffs.first().diffAsString == """
			diff --git a/bar.txt b/bar.txt
			index 5ce4103..4941f15 100644
			--- a/bar.txt
			+++ b/bar.txt
			@@ -1 +1 @@
			-bar!
			\\ No newline at end of file
			+bar!monkey!
			\\ No newline at end of file
		""".replaceFirst('\n','').stripIndent()
	}

	def 'can show diffs in commit that deleted existing file'() {
		File fooFile = repoFile("bar.txt")
		fooFile << "bar!"
		grgit.add(patterns: ['.'])
		grgit.commit(message: "Initial commit")

		// Delete existing file
		grgit.remove(patterns: ['bar.txt'])
		Commit removeCommit = grgit.commit(message: "Deleted file")

		when:
		CommitDiff diff = grgit.show(commit: removeCommit)

		then:
		diff.commit == removeCommit
		diff.diffs.size() == 1
		diff.diffs.first().fileName == "bar.txt"
		diff.diffs.first().changeType == DiffEntry.ChangeType.DELETE
		!diff.diffs.first().diffAsString
	}

	def 'can show diffs in commit with multiple changes'() {
		File animalFile = repoFile("animals.txt")
		animalFile << "giraffe!"
		grgit.add(patterns: ['.'])
		grgit.commit(message: "Initial commit")

		// Change existing file
		animalFile << "zebra!"

		// Add new file
		File fishFile = repoFile("salmon.txt")
		fishFile<< "salmon!"
		grgit.add(patterns: ['.'])
		Commit changeCommit = grgit.commit(message: "Add fish and update animals with zebra")

		when:
		CommitDiff diff = grgit.show(commit: changeCommit)

		then:
		diff.commit == changeCommit
		diff.diffs.size() == 2
		diff.diffs.find {it.fileName == "animals.txt"}.changeType == DiffEntry.ChangeType.MODIFY
		diff.diffs.find {it.fileName == "salmon.txt"}.changeType == DiffEntry.ChangeType.ADD
	}

	def 'can show diffs based on rev string'() {
		File fooFile = repoFile("foo.txt")
		fooFile << "foo!"
		grgit.add(patterns: ['.'])
		Commit commit = grgit.commit(message: "Initial commit")

		when:
		CommitDiff diff = grgit.show(commit: commit.id)

		then:
		diff.commit == commit
		diff.diffs.size() == 1
		diff.diffs.first().fileName == "foo.txt"
		diff.diffs.first().changeType == DiffEntry.ChangeType.ADD
		diff.diffs.diffAsString
	}
}