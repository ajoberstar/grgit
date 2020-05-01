package org.ajoberstar.grgit.operation

import java.util.concurrent.Callable

import org.ajoberstar.grgit.DiffEntry
import org.ajoberstar.grgit.Repository
import org.ajoberstar.grgit.internal.Operation
import org.ajoberstar.grgit.service.ResolveService
import org.ajoberstar.grgit.util.JGitUtil
import org.eclipse.jgit.api.DiffCommand
import org.eclipse.jgit.lib.AnyObjectId
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.filter.PathFilter

/**
 * Show changed files between commits.
 * Returns changes made in commit in the form of {@link org.ajoberstar.grgit.DiffEntry}.
 * @since 4.1.0
 * @see <a href="http://ajoberstar.org/grgit/grgit-diff.html">grgit-diff</a>
 * @see <a href="http://git-scm.com/docs/git-diff">git-diff Manual Page</a>
 */
@Operation('diff')
class DiffOp implements Callable<List<DiffEntry>> {
  private final Repository repo

  /**
   * The commit to diff against, default HEAD.
   * @see {@link org.ajoberstar.grgit.service.ResolveService#toRevisionString(Object)}
   */
  Object oldCommit
  /**
   * The commit to diff against, default HEAD.
   * @see {@link org.ajoberstar.grgit.service.ResolveService#toRevisionString(Object)}
   */
  Object newCommit
  /**
   * Used to limit the diff to the named path
   */
  String pathFilter

  DiffOp(Repository repo) {
    this.repo = repo
  }

  List<DiffEntry> call() {
    DiffCommand cmd = repo.jgit.diff()
    ResolveService resolve = new ResolveService(repo)
    def toObjectId = { rev ->
      String revstr = resolve.toRevisionString(rev)
      def id = JGitUtil.resolveRevObject(repo, revstr, true)
      if (id) {
        return id
      } else {
        throw new IllegalArgumentException("\"${revstr}\" cannot be resolved to an object in this repository.")
      }
    }

    cmd.showNameAndStatusOnly = true
    if (pathFilter) {
      cmd.setPathFilter(PathFilter.create(pathFilter))
    }
    if (oldCommit) {
      cmd.oldTree = prepareTreeParser(repo.jgit.repository, toObjectId(oldCommit))
      if (newCommit) {
        cmd.newTree = prepareTreeParser(repo.jgit.repository, toObjectId(newCommit))
      }
    }
    List<org.eclipse.jgit.diff.DiffEntry> jgitEntries = cmd.call()
    List<DiffEntry> entries = []
    jgitEntries.each { entry ->
      entries.add(new DiffEntry(
        changeType: convertChangeType(entry.changeType),
        oldPath: entry.oldPath,
        newPath: entry.newPath))
    }
    return entries
  }


  private AbstractTreeIterator prepareTreeParser(org.eclipse.jgit.lib.Repository repository, AnyObjectId objectId)  {
    // from the commit we can build the tree which allows us to construct the TreeParser
    RevWalk walk = new RevWalk(repository)
    RevCommit commit = walk.parseCommit(objectId);
    RevTree tree = walk.parseTree(commit.getTree().getId());
    CanonicalTreeParser treeParser = new CanonicalTreeParser()
    ObjectReader reader = repository.newObjectReader()
    treeParser.reset(reader, tree.getId());
    walk.dispose();
    return treeParser;
  }

  private DiffEntry.ChangeType convertChangeType(org.eclipse.jgit.diff.DiffEntry.ChangeType changeType) {
    return DiffEntry.ChangeType.valueOf(changeType.name())
  }
}
