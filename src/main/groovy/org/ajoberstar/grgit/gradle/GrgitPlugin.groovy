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
