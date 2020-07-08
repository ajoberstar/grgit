package org.ajoberstar.grgit.gradle

import org.ajoberstar.grgit.Grgit
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ConfigCacheTest extends Specification {
    @Rule TemporaryFolder tempDir = new TemporaryFolder()
    File projectDir
    File buildFile

    def setup() {
        projectDir = tempDir.newFolder('project')
        buildFile = projectFile('build.gradle')
    }

    def "grgit build service can be fetched from registered services"() {
        given:
        buildFile << """
            import org.ajoberstar.grgit.gradle.GrgitBuildService

            plugins {
                id 'org.ajoberstar.grgit'
            }

            task doStuff {
              def injected = project.gradle.sharedServices.registrations.getByName("grgit").getService()
              doLast {
                assert injected.get().grgit == null
              }
            }
        """

        when:
        runner()
                .withArguments('--configuration-cache', 'doStuff')
                .build()

        and:
        def result = runner()
                .withArguments('--configuration-cache', 'doStuff')
                .build()

        then:
        result.output.contains('Reusing configuration cache.')
    }


    def 'with repo, plugin opens the repo as grgit'() {
        given:
        Grgit git = Grgit.init(dir: projectDir)
        projectFile('1.txt') << '1'
        git.add(patterns: ['1.txt'])
        git.commit(message: 'yay')
        git.tag.add(name: '1.0.0')

        buildFile << '''\
plugins {
  id 'org.ajoberstar.grgit'
}

task doStuff {
  def injected = project.grgitExtension
  doLast {
    println injected.describe()
  }
}
'''
        when:
        runner()
                .withArguments('--configuration-cache', 'doStuff')
                .build()

        and:
        def result = runner()
                .withArguments('--configuration-cache', 'doStuff')
                .build()
        then:
        result.task(':doStuff').outcome == TaskOutcome.SUCCESS
        result.output.contains('Reusing configuration cache.')
        result.output.contains('1.0.0\n')
    }

    private GradleRunner runner(String... args) {
        return GradleRunner.create()
                .withGradleVersion("6.6-milestone-3")
                .withPluginClasspath()
                .withProjectDir(projectDir)
                .forwardOutput()
                .withArguments((args + '--stacktrace') as String[])
    }

    private File projectFile(String path) {
        File file = new File(projectDir, path)
        file.parentFile.mkdirs()
        return file
    }
}
