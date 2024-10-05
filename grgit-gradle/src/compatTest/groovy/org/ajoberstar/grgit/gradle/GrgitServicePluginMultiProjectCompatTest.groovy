package org.ajoberstar.grgit.gradle

import spock.lang.Specification

import org.ajoberstar.grgit.Grgit
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.TempDir

class GrgitServicePluginMultiProjectCompatTest extends Specification {
    @TempDir File tempDir
    File projectDir
    File settingsFile
    File buildRootFile
    File build1File
    File build2File

    def setup() {
        projectDir = new File(tempDir, 'project')
        settingsFile = projectFile('settings.gradle')
        settingsFile << """\
pluginManagement {
  repositories {
    mavenCentral()
    mavenLocal()
  }
}

include 'sub1', 'sub2'
"""

        build1File = projectFile('sub1/build.gradle')
        build1File << """\
import org.ajoberstar.grgit.gradle.GrgitService

plugins {
  id 'org.ajoberstar.grgit.service' version '${System.properties['compat.plugin.version']}'
}
  
tasks.register("doStuff", DoStuffTask.class) {
    service = grgitService.service
}

class DoStuffTask extends DefaultTask {
    @Input
    final Property<GrgitService> service

    @Inject
    DoStuffTask(ObjectFactory objectFactory) {
        this.service = objectFactory.property(GrgitService.class);
    }

    @TaskAction
    void execute() {
        println service.get().grgit.describe()
    }
}
"""

        build2File = projectFile('sub2/build.gradle')
        build2File << """\
import org.ajoberstar.grgit.gradle.GrgitService

plugins {
  id 'org.ajoberstar.grgit.service' version '${System.properties['compat.plugin.version']}'
}

tasks.register("doStuff", DoStuffTask.class) {
    service = grgitService.service
}

class DoStuffTask extends DefaultTask {
    @Input
    final Property<GrgitService> service

    @Inject
    DoStuffTask(ObjectFactory objectFactory) {
        this.service = objectFactory.property(GrgitService.class);
    }

    @TaskAction
    void execute() {
        println service.get().grgit.describe()
    }
}
"""
    }

    def 'with no repo, accessing service fails'() {
        given:
        // nothing
        when:
        def result = buildAndFail('doStuff', '--no-configuration-cache')
        then:
        result.task(':sub1:doStuff')?.outcome in [null, TaskOutcome.FAILED]
        result.task(':sub2:doStuff')?.outcome in [null, TaskOutcome.FAILED]
    }

    def 'with repo, plugin opens the repo as grgit'() {
        given:
        Grgit git = Grgit.init(dir: projectDir)
        projectFile('1.txt') << '1'
        git.add(patterns: ['1.txt'])
        git.commit(message: 'yay')
        git.tag.add(name: '1.0.0')
        when:
        def result = build('doStuff', '--quiet', '--no-configuration-cache')
        then:
        result.task(':sub1:doStuff')?.outcome == TaskOutcome.SUCCESS
        result.task(':sub2:doStuff')?.outcome == TaskOutcome.SUCCESS
        result.output.normalize() == '1.0.0\n1.0.0\n'
    }

    def 'with repo, plugin closes the repo after build is finished'() {
        given:
        Grgit git = Grgit.init(dir: projectDir)
        projectFile('1.txt') << '1'
        git.add(patterns: ['1.txt'])
        git.commit(message: 'yay')
        git.tag.add(name: '1.0.0')
        when:
        def result = build('doStuff', '--info', '--no-configuration-cache')
        then:
        result.task(':sub1:doStuff')?.outcome == TaskOutcome.SUCCESS
        result.task(':sub2:doStuff')?.outcome == TaskOutcome.SUCCESS
        result.output.contains('Closing Git repo')
    }

    private BuildResult build(String... args) {
        return GradleRunner.create()
                .withGradleVersion(System.properties['compat.gradle.version'])
                .withProjectDir(projectDir)
                .forwardOutput()
                .withArguments((args + '--stacktrace') as String[])
                .build()
    }

    private BuildResult buildAndFail(String... args) {
        return GradleRunner.create()
                .withGradleVersion(System.properties['compat.gradle.version'])
                .withProjectDir(projectDir)
                .forwardOutput()
                .withArguments((args + '--stacktrace') as String[])
                .buildAndFail()
    }

    private File projectFile(String path) {
        File file = new File(projectDir, path)
        file.parentFile.mkdirs()
        return file
    }
}
