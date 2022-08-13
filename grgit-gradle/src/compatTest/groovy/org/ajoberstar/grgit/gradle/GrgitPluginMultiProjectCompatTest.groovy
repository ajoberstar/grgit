package org.ajoberstar.grgit.gradle

import spock.lang.Specification

import org.ajoberstar.grgit.Grgit
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.TempDir

class GrgitPluginMultiProjectCompatTest extends Specification {
  @TempDir File tempDir
  File projectDir
    File settingsFile
    File buildRootFile
    File build1File
    File build2File

  def setup() {
    projectDir = new File(tempDir, 'project')
        settingsFile = projectFile('settings.gradle')
        settingsFile << '''\
include 'sub1', 'sub2'
'''

//         buildRootFile = projectFile('build.gradle')
//         buildRootFile << '''\
// plugins {
//   id 'org.ajoberstar.grgit'
// }

// task checkGrgitNull {
//   doLast {
//     assert grgit == null
//   }
// }

// task describeGrgit {
//   doLast {
//     println 'From root'
//     println grgit.describe()
//   }
// }
// '''

        build1File = projectFile('sub1/build.gradle')
        build1File << '''\
plugins {
  id 'org.ajoberstar.grgit'
}

task checkGrgitNull {
  doLast {
    assert grgit == null
  }
}

task describeGrgit {
  doLast {
    println 'From sub1'
    println grgit.describe()
  }
}
'''

        build2File = projectFile('sub2/build.gradle')
        build2File << '''\
plugins {
  id 'org.ajoberstar.grgit'
}

task checkGrgitNull {
  doLast {
    assert grgit == null
  }
}

task describeGrgit {
  doLast {
    println 'From sub2'
    println grgit.describe()
  }
}
'''
  }

  def 'with no repo, plugin sets grgit to null'() {
    when:
    def result = build('checkGrgitNull', '--configuration-cache')
    then:
    // result.task(':checkGrgitNull').outcome == TaskOutcome.SUCCESS
    result.task(':sub1:checkGrgitNull').outcome == TaskOutcome.SUCCESS
    result.task(':sub2:checkGrgitNull').outcome == TaskOutcome.SUCCESS
  }

  def 'with repo, plugin opens the repo as grgit'() {
    given:
    Grgit git = Grgit.init(dir: projectDir)
    projectFile('1.txt') << '1'
    git.add(patterns: ['1.txt'])
    git.commit(message: 'yay')
    git.tag.add(name: '1.0.0')
    when:
    def result = build('describeGrgit', '--quiet', '--configuration-cache')
    then:
    // result.task(':describeGrgit').outcome == TaskOutcome.SUCCESS
    result.task(':sub1:describeGrgit').outcome == TaskOutcome.SUCCESS
    result.task(':sub2:describeGrgit').outcome == TaskOutcome.SUCCESS
    // result.output.normalize() == 'From root\n1.0.0\nFrom sub1\n1.0.0\nFrom sub2\n1.0.0\n'
    result.output.normalize() == 'From sub1\n1.0.0\nFrom sub2\n1.0.0\n'
  }

  def 'with repo, plugin closes the repo after build is finished'() {
    given:
    Grgit git = Grgit.init(dir: projectDir)
    projectFile('1.txt') << '1'
    git.add(patterns: ['1.txt'])
    git.commit(message: 'yay')
    git.tag.add(name: '1.0.0')
    when:
    def result = build('describeGrgit', '--info', '--configuration-cache')
    then:
    // result.task(':describeGrgit').outcome == TaskOutcome.SUCCESS
    result.task(':sub1:describeGrgit').outcome == TaskOutcome.SUCCESS
    result.task(':sub2:describeGrgit').outcome == TaskOutcome.SUCCESS
    result.output.contains('Closing Git repo')
  }

  private BuildResult build(String... args) {
    return GradleRunner.create()
      .withGradleVersion(System.properties['compat.gradle.version'])
      .withPluginClasspath()
      .withProjectDir(projectDir)
      .forwardOutput()
      .withArguments((args + '--stacktrace') as String[])
      .build()
  }

  private File projectFile(String path) {
    File file = new File(projectDir, path)
    file.parentFile.mkdirs()
    return file
  }
}
