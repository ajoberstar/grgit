/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ajoberstar.grgit.gradle

import spock.lang.Specification
import spock.lang.Unroll

import org.ajoberstar.grgit.Grgit
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder

class BaseCompatTest extends Specification {
  @Rule TemporaryFolder tempDir = new TemporaryFolder()
  File projectDir
  File buildFile

  def setup() {
    projectDir = tempDir.newFolder('project')
    buildFile = projectFile('build.gradle')

  }

  def 'with no repo, plugin sets grgit to null'() {
    given:
    buildFile << '''\
plugins {
  id 'org.ajoberstar.grgit'
}

task doStuff {
  doLast {
    assert grgit == null
  }
}
'''
    when:
    def result = build('doStuff')
    then:
    result.task(':doStuff').outcome == TaskOutcome.SUCCESS
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
  doLast {
    println grgit.describe()
  }
}
'''
    when:
    def result = build('doStuff', '--quiet')
    then:
    result.task(':doStuff').outcome == TaskOutcome.SUCCESS
    result.output.normalize() == '1.0.0\n'
  }

  def 'with repo, plugin closes the repo after build is finished'() {
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
  doLast {
    println grgit.describe()
  }
}
'''
    when:
    def result = build('doStuff', '--info')
    then:
    result.task(':doStuff').outcome == TaskOutcome.SUCCESS
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
