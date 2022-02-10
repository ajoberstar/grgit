package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec

class RemoveRemoveOpSpec extends SimpleGitOpSpec {
    def 'remote with given name is removed'() {
        given:
        grgit.remote.add(name: 'newRemote', url: 'http://fetch.url/')
        when:
        grgit.remote.remove(name: 'newRemote')
        then:
        [] == grgit.remote.list()
    }
}
