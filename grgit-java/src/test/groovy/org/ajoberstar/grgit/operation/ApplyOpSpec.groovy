package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec

class ApplyOpSpec extends SimpleGitOpSpec {
  def 'apply with no patch fails'() {
    when:
    grgit.apply()
    then:
    thrown(IllegalStateException)
  }

  def 'apply with patch succeeds'() {
    given:
    repoFile('1.txt') << 'something'
    repoFile('2.txt') << 'something else\n'
    grgit.add(patterns:['.'])
    grgit.commit(message: 'Test')
    def patch = tempDir.newFile()
    this.class.getResourceAsStream('/org/ajoberstar/grgit/operation/sample.patch').withStream { stream ->
      patch << stream
    }
    when:
    grgit.apply(patch: patch)
    then:
    repoFile('1.txt').text == 'something'
    repoFile('2.txt').text == 'something else\nis being added\n'
    repoFile('3.txt').text == 'some new stuff\n'
  }
}
