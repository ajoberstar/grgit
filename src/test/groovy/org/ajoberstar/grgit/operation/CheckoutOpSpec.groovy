package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec
import org.eclipse.jgit.api.errors.GitAPIException

class CheckoutOpSpec extends SimpleGitOpSpec {
  def setup() {
    repoFile('1.txt') << '1'
    grgit.add(patterns: ['1.txt'])
    grgit.commit(message: 'do')

    repoFile('1.txt') << '2'
    grgit.add(patterns: ['1.txt'])
    grgit.commit(message: 'do')

    grgit.branch.add(name: 'my-branch')

    repoFile('1.txt') << '3'
    grgit.add(patterns: ['1.txt'])
    grgit.commit(message: 'do')
  }

  def 'checkout with existing branch and createBranch false works'() {
    when:
    grgit.checkout(branch: 'my-branch')
    then:
    grgit.head() == grgit.resolve.toCommit('my-branch')
    grgit.branch.current.fullName == 'refs/heads/my-branch'
    grgit.log().size() == 2
    repoFile('1.txt').text == '12'
  }

  def 'checkout with existing branch, createBranch true fails'() {
    when:
    grgit.checkout(branch: 'my-branch', createBranch: true)
    then:
    thrown(GitAPIException)
  }

  def 'checkout with non-existent branch and createBranch false fails'() {
    when:
    grgit.checkout(branch: 'fake')
    then:
    thrown(GitAPIException)
  }

  def 'checkout with non-existent branch and createBranch true works'() {
    when:
    grgit.checkout(branch: 'new-branch', createBranch: true)
    then:
    grgit.branch.current.fullName == 'refs/heads/new-branch'
    grgit.head() == grgit.resolve.toCommit('master')
    grgit.log().size() == 3
    repoFile('1.txt').text == '123'
  }

  def 'checkout with non-existent branch, createBranch true, and startPoint works'() {
    when:
    grgit.checkout(branch: 'new-branch', createBranch: true, startPoint: 'my-branch')
    then:
    grgit.branch.current.fullName == 'refs/heads/new-branch'
    grgit.head() == grgit.resolve.toCommit('my-branch')
    grgit.log().size() == 2
    repoFile('1.txt').text == '12'
  }

  def 'checkout with no branch name and createBranch true fails'() {
    when:
    grgit.checkout(createBranch: true)
    then:
    thrown(IllegalArgumentException)
  }

  def 'checkout with existing branch and orphan true fails'() {
    when:
    grgit.checkout(branch: 'my-branch', orphan: true)
    then:
    thrown(GitAPIException)
  }

  def 'checkout with non-existent branch and orphan true works'() {
    when:
    grgit.checkout(branch: 'orphan-branch', orphan: true)
    then:
    grgit.branch.current.fullName == 'refs/heads/orphan-branch'
    grgit.status() == new Status(staged: [added: ['1.txt']])
    repoFile('1.txt').text == '123'
  }

  def 'checkout with non-existent branch, orphan true, and startPoint works'() {
    when:
    grgit.checkout(branch: 'orphan-branch', orphan: true, startPoint: 'my-branch')
    then:
    grgit.branch.current.fullName == 'refs/heads/orphan-branch'
    grgit.status() == new Status(staged: [added: ['1.txt']])
    repoFile('1.txt').text == '12'
  }

  def 'checkout with no branch name and orphan true fails'() {
    when:
    grgit.checkout(orphan: true)
    then:
    thrown(IllegalArgumentException)
  }
}
