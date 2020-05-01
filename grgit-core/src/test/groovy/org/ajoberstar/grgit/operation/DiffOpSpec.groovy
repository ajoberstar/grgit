package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.CommitDiff
import org.ajoberstar.grgit.DiffEntry
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec

class DiffOpSpec extends SimpleGitOpSpec {

  def 'can show diffs in commit that added new file'() {
    File fooFile = repoFile("dir0/foo1.txt")
    fooFile << "foo!"
    grgit.add(patterns: ['.'])
    Commit commit = grgit.commit(message: "Initial commit")

    File fooFile2 = repoFile("dir0/foo2.txt")
    fooFile2 << "foo!"
    grgit.add(patterns: ['.'])
    Commit commit2 = grgit.commit(message: "Initial commit")

    expect:
    grgit.diff(oldCommit: commit)[0] == new DiffEntry(
      changeType: DiffEntry.ChangeType.ADD,
      oldPath: '/dev/null',
      newPath: 'dir0/foo2.txt'
    )
  }

  def 'can show diffs in commit that modified existing file'() {
    File fooFile = repoFile("dir1/foo.txt")
    fooFile << "foo!"
    grgit.add(patterns: ['.'])
    Commit commit = grgit.commit(message: "Initial commit")
    // modify the file and commit again
    fooFile << "foo!!!"
    grgit.add(patterns: ['.'])
    Commit commit2 = grgit.commit(message: "Second commit")

    expect:
    grgit.diff(oldCommit: commit)[0] == new DiffEntry(
      changeType: DiffEntry.ChangeType.MODIFY,
      oldPath: 'dir1/foo.txt',
      newPath: 'dir1/foo.txt'
    )
  }

  def 'can show diffs between two commits that modified existing file usig pathFilter'() {
    File fooFile = repoFile("dir2/foo.txt")
    fooFile << "foo!"
    grgit.add(patterns: ['.'])
    Commit commit = grgit.commit(message: "Initial commit")
    // modify the file and commit again
    fooFile << "foo!!!"
    grgit.add(patterns: ['.'])
    Commit commit2 = grgit.commit(message: "Second commit")

    expect:
    grgit.diff(oldCommit: commit, newCommit: commit2, pathFilter: 'dir2/foo.txt')[0] == new DiffEntry(
      changeType: DiffEntry.ChangeType.MODIFY,
      oldPath: 'dir2/foo.txt',
      newPath: 'dir2/foo.txt'
    )
  }


  def 'can show diffs in commit that deleted existing file'() {
    File fooFile = repoFile("bar.txt")
    fooFile << "bar!"
    grgit.add(patterns: ['.'])
    Commit commit = grgit.commit(message: "Initial commit")

    // Delete existing file
    grgit.remove(patterns: ['bar.txt'])
    Commit removeCommit = grgit.commit(message: "Deleted file")

    expect:
    grgit.diff(oldCommit: commit)[0] == new DiffEntry(
      changeType: DiffEntry.ChangeType.DELETE,
      oldPath: 'bar.txt',
      newPath: '/dev/null'
    )
  }

}
