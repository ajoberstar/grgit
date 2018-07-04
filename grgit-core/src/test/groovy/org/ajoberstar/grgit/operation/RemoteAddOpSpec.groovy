package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.Remote
import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec

class RemoteAddOpSpec extends SimpleGitOpSpec {

  def 'remote with given name and push/fetch urls is added'() {
    given:
    Remote remote = new Remote(
      name: 'newRemote',
      url: 'http://fetch.url/',
      fetchRefSpecs: ['+refs/heads/*:refs/remotes/newRemote/*'])
    expect:
    remote == grgit.remote.add(name: 'newRemote', url: 'http://fetch.url/')
    [remote] == grgit.remote.list()
  }
}
