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
    def patch = new File(tempDir, 'temp.patch')
    this.class.getResourceAsStream('/org/ajoberstar/grgit/operation/sample.patch').withStream { stream ->
      patch << stream
    }
    when:
    grgit.apply(patch: patch)
    then:
    repoFile('1.txt').text.normalize() == 'something'
    repoFile('2.txt').text.normalize() == 'something else\nis being added\n'
    repoFile('3.txt').text.normalize() == 'some new stuff\n'
  }
}
