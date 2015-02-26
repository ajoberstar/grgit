package org.ajoberstar.grgit.operation

import org.ajoberstar.grgit.fixtures.SimpleGitOpSpec


class DescribeOpSpec extends SimpleGitOpSpec {
    def setup() {
        grgit.commit(message:"initial commit")
        grgit.tag.add(name:"initial")
    }

    def 'with initial tag'() {
        expect:
        grgit.describe() == "initial"
    }

    def 'with additional commit'(){
        when:
        repoFile('1.txt') << '1'
        grgit.add(patterns:['1.txt'])
        grgit.commit(message:  "another commit")
        then:
        grgit.describe().startsWith("initial-1-")
    }
}

