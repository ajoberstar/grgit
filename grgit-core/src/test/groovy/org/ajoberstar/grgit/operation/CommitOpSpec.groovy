package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Person
import org.ajoberstar.grgit.Status
import org.ajoberstar.grgit.fixtures.GitTestUtil
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec
import org.eclipse.jgit.api.errors.ServiceUnavailableException

class CommitOpSpec extends SimpleGitOpSpec {
  def setup() {
    GitTestUtil.configure(grgit) {
      setString('user', null, 'name', 'Alfred Pennyworth')
      setString('user', null, 'email', 'alfred.pennyworth@wayneindustries.com')
    }

    repoFile('1.txt') << '1'
    repoFile('2.txt') << '1'
    repoFile('folderA/1.txt') << '1'
    repoFile('folderA/2.txt') << '1'
    repoFile('folderB/1.txt') << '1'
    repoFile('folderC/1.txt') << '1'
    grgit.add(patterns:['.'])
    grgit.commit(message: 'Test')
    repoFile('1.txt') << '2'
    repoFile('folderA/1.txt') << '2'
    repoFile('folderA/2.txt') << '2'
    repoFile('folderB/1.txt') << '2'
    repoFile('folderB/2.txt') << '2'
  }

  def 'commit with all false commits changes from index'() {
    given:
    grgit.add(patterns:['folderA'])
    when:
    grgit.commit(message:'Test2')
    then:
    grgit.log().size() == 2
    grgit.status() == new Status(
      unstaged: [added: ['folderB/2.txt'], modified: ['1.txt', 'folderB/1.txt']])
  }

  def 'commit with all true commits changes in previously tracked files'() {
    when:
    grgit.commit(message:'Test2', all: true)
    then:
    grgit.log().size() == 2
    grgit.status() == new Status(
      unstaged: [added: ['folderB/2.txt']])
  }

  def 'commit amend changes the previous commit'() {
    given:
    grgit.add(patterns:['folderA'])
    when:
    grgit.commit(message:'Test2', amend: true)
    then:
    grgit.log().size() == 1
    grgit.status() == new Status(
      unstaged: [added: ['folderB/2.txt'], modified: ['1.txt', 'folderB/1.txt']])
  }

  def 'commit with paths only includes the specified paths from the index'() {
    given:
    grgit.add(patterns:['.'])
    when:
    grgit.commit(message:'Test2', paths:['folderA'])
    then:
    grgit.log().size() == 2
    grgit.status() == new Status(
      staged: [added: ['folderB/2.txt'], modified: ['1.txt', 'folderB/1.txt']])
  }

  def 'commit without specific committer or author uses repo config'() {
    given:
    grgit.add(patterns:['folderA'])
    when:
    def commit = grgit.commit(message:'Test2')
    then:
    commit.committer == new Person('Alfred Pennyworth', 'alfred.pennyworth@wayneindustries.com')
    commit.author == new Person('Alfred Pennyworth', 'alfred.pennyworth@wayneindustries.com')
    grgit.log().size() == 2
    grgit.status() == new Status(
      unstaged: [added: ['folderB/2.txt'], modified: ['1.txt', 'folderB/1.txt']])
  }

  def 'commit with specific committer and author uses those'() {
    given:
    grgit.add(patterns:['folderA'])
    def bruce = new Person('Bruce Wayne', 'bruce.wayne@wayneindustries.com')
    def lucius = new Person('Lucius Fox', 'lucius.fox@wayneindustries.com')
    when:
    def commit = grgit.commit {
      message = 'Test2'
      committer = lucius
      author = bruce
    }
    then:
    commit.committer == lucius
    commit.author == bruce
    grgit.log().size() == 2
    grgit.status() == new Status(
      unstaged: [added: ['folderB/2.txt'], modified: ['1.txt', 'folderB/1.txt']])
  }

  def 'commit with sign=true tries to sign the commit'() {
    given:
      grgit.add(patterns:['folderA'])
    when:
      grgit.commit(message:'Rest (signed)', sign: true)
    then:
      thrown(ServiceUnavailableException)
  }

  def 'commit with sign=false overrides "[commit] gpgSign=true" from .gitconfig'() {
    given:
      GitTestUtil.configure(grgit) {
        setBoolean('commit', null, 'gpgSign', true)
      }
      grgit.add(patterns:['.'])
    when:
      grgit.commit(message:'Rest (unsigned)', sign: false)
    then:
      grgit.log().size() == 2
  }
}
