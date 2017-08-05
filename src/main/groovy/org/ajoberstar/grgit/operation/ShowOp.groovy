/*
 * Copyright 2012-2017 the original author or authors.
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

import org.ajoberstar.grgit.CommitDiff
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.service.ResolveService
import org.ajoberstar.grgit.util.JGitUtil
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.RenameDetector
import org.eclipse.jgit.diff.DiffEntry.ChangeType
import org.eclipse.jgit.treewalk.TreeWalk

/**
 * Show changes made in a commit.
 * Returns changes made in commit in the form of {@link org.ajoberstar.grgit.CommitDiff}.
 * @since 1.2.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-show.html">grgit-show</a>
 * @see <a href="http://git-scm.com/docs/git-show">git-show Manual Page</a>
 */
@Operation('show')
class ShowOp implements Callable<CommitDiff> {
  private final Repository repo

  /**
   * The commit to show
   * @see {@link org.ajoberstar.grgit.service.ResolveService#toRevisionString(Object)}
   */
  Object commit

  ShowOp(Repository repo) {
    this.repo = repo
  }

  CommitDiff call() {
    if (!commit) {
      throw new IllegalArgumentException('You must specify which commit to show')
    }
    def revString = new ResolveService(repo).toRevisionString(commit)
    def commitId = JGitUtil.resolveRevObject(repo, revString)
    def parentId = JGitUtil.resolveParents(repo, commitId).find()

    def commit = JGitUtil.resolveCommit(repo, commitId)

    TreeWalk walk = new TreeWalk(repo.jgit.repository)
    walk.recursive = true

    if (parentId) {
      walk.addTree(parentId.tree)
      walk.addTree(commitId.tree)
      List initialEntries = DiffEntry.scan(walk)
      RenameDetector detector = new RenameDetector(repo.jgit.repository)
      detector.addAll(initialEntries)
      List entries = detector.compute()
      Map entriesByType = entries.groupBy { it.changeType }

      return new CommitDiff(
        commit: commit,
        added: entriesByType[ChangeType.ADD].collect { it.newPath },
        copied: entriesByType[ChangeType.COPY].collect { it.newPath },
        modified: entriesByType[ChangeType.MODIFY].collect { it.newPath },
        removed: entriesByType[ChangeType.DELETE].collect { it.oldPath },
        renamed: entriesByType[ChangeType.RENAME].collect { it.newPath }
      )
    } else {
      walk.addTree(commitId.tree)
      def added = []
      while (walk.next()) {
        added << walk.pathString
      }
      return new CommitDiff(
        commit: commit,
        added: added
      )
    }
  }
}
