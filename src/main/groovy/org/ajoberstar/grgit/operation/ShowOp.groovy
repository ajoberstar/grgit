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

import org.ajoberstar.grgit.CommitDiff
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.exception.GrgitException
import org.ajoberstar.grgit.service.ResolveService
import org.ajoberstar.grgit.util.JGitUtil
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffEntry.ChangeType
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk

import java.util.concurrent.Callable

/**
 * Show changes made in a commit.
 * Returns changes made in commit in the form of {@link org.ajoberstar.grgit.CommitDiff}.
 *
 * <p>Show changes based on revision string</p>
 *
 * <pre>
 * CommitDiff diff = grgit.show(commit: 'c99a58906246e96333e0788fc50eb8e3e6df0bd9')
 * </pre>
 *
 * <p>Show changes based on Commit reference</p>
 *
 * <pre>
 * Commit myCommit = grgit.commit(message: "Add stuff")
 * CommitDiff diff = grgit.show(commit: myCommit)
 * </pre>
 *
 * @see <a href="http://git-scm.com/docs/git-show">git-show Manual Page</a>
 */
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
            throw new GrgitException("You must specify which commit to show")
        }
        def revString = new ResolveService(repo).toRevisionString(commit)
        def revObject = JGitUtil.resolveRevObject(repo, revString)

        return new CommitDiff(
                commit: JGitUtil.convertCommit(revObject),
                diffs: this.getFilesInCommit(repo, revObject))
    }

    /**
     * Returns the list of files changed in a specified commit. If the
     * repository does not exist or is empty, an empty list is returned.
     *
     * @param repository git commit
     * @param commit if null, HEAD is used.
     * @return The diffs in a commit
     */
    public static List<CommitDiff.Diff> getFilesInCommit(Repository repository, RevCommit commit) {

        if (!hasCommits(repository)) {
            return [];
        }
        RevWalk rw = new RevWalk(repository.jgit.repository)
        try {
            if (!commit) {
                ObjectId object = head(repository)
                commit = rw.parseCommit(object)
            }

            def isFirstCommit = commit.getParentCount() == 0
            if (isFirstCommit) {
                return firstCommit(repository, commit)
            } else {
                return diffCommit(rw, commit, repository)
            }
        } catch (Throwable t) {
            throw new GrgitException('Failed to determine files in commit..', t)
        } finally {
            rw.dispose()
        }
    }

    private static List<CommitDiff.Diff> diffCommit(RevWalk rw, RevCommit commit, Repository repository) {
        def outputStream = new ByteArrayOutputStream()
        def df = new DiffFormatter(outputStream)
        df.setRepository(repository.jgit.repository)
        df.setDiffComparator(RawTextComparator.DEFAULT)
        df.setDetectRenames(true)
        RevCommit parent = rw.parseCommit(commit.getParent(0).getId())
        List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree())
        return diffs.collect {
            df.format(it)   // Write diff to the outputstream
            return JGitUtil.convertDiff(it, outputStream.toString())
        }
    }

    private static List<CommitDiff.Diff> firstCommit(Repository repository, RevCommit commit) {
        TreeWalk tw = new TreeWalk(repository.jgit.repository)
        tw.reset()
        tw.setRecursive(true)
        tw.addTree(commit.getTree())
        def list = []
        while (tw.next()) {
            def content = new String(repository.jgit.repository.newObjectReader().open(tw.getObjectId(0)).getBytes())
            list.add(new CommitDiff.Diff(
                    tw.getPathString(),
                    tw.getObjectId(0).getName(),
                    ChangeType.ADD,
                    content))
        }
        tw.release()
        return list
    }

    private static boolean hasCommits(Repository repository) {
        !repository.jgit.log().call().asList().isEmpty()
    }

    /**
     * Returns head of the repo
     * @param repository The jgit repo
     * @return The head
     */
    public static ObjectId head(Repository repository) {
        ObjectId object = repository.jgit.repository.resolve(Constants.HEAD)
        if (object == null) {
            throw new GrgitException('There is no HEAD')
        }
        return object
    }
}