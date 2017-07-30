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

import org.ajoberstar.grgit.Grgit
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin adding a {@code grgit} property to all projects
 * that searches for a Git repo from the project's
 * directory.
 * @since 2.0.0
 */
class GrgitPlugin implements Plugin<Project> {
  @Override
  void apply(Project project) {
    try {
      Grgit grgit = Grgit.open(currentDir: project.rootDir)

      // Make sure Git repo is closed when the build is over. Ideally, this avoids issues with the daemon.
      project.gradle.buildFinished {
        project.logger.info "Closing Git repo: ${grgit.repository.rootDir}"
        grgit.close()
      }

      project.allprojects { prj ->
        if (prj.ext.has('grgit')) {
          prj.logger.warn("Project ${prj.path} already has a grgit property. Remove org.ajoberstar.grgit from either ${prj.path} or ${project.path}.")
        }
        prj.ext.grgit = grgit
      }
    } catch (Exception e) {
      project.logger.error("No git repository found for ${project.path}. Accessing grgit will cause an NPE.")
      project.logger.debug("Failed trying to find git repository for ${project.path}", e)
      project.ext.grgit = null
    }
  }
}
