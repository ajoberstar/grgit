package org.ajoberstar.grgit.gradle

import org.ajoberstar.grgit.Grgit
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.util.GradleVersion

/**
 * Plugin adding a {@code grgit} property to all projects
 * that searches for a Git repo from the project's
 * directory.
 * @since 2.0.0
 */
class GrgitPlugin implements Plugin<Project> {
  @Override
  void apply(Project project) {
    if (GradleVersion.current() >= GradleVersion.version("6.1")) {
      Provider<GrgitBuildService> provider = project.gradle.sharedServices.registerIfAbsent("grgit", GrgitBuildService, { spec ->
        spec.parameters.rootDirectory = project.rootDir
      })

      if (provider.get().grgit != null) {
        project.allprojects {
          project.extensions.add(Grgit, 'grgit', provider.get().grgit)
          project.extensions.create('grgitExtension', GrgitExtension, provider)
        }
      } else {
        project.allprojects {
          project.extensions.add(Grgit, 'grgit', null)
        }
      }
    } else {
      try {
        Grgit grgit = Grgit.open(currentDir: project.rootDir)

        // Make sure Git repo is closed when the build is over. Ideally, this avoids issues with the daemon.
        project.gradle.buildFinished {
          project.logger.info "Closing Git repo: ${grgit.repository.rootDir}"
          grgit.close()
        }

        project.allprojects { Project prj ->
          if (prj.extensions.hasProperty('grgit')) {
            prj.logger.warn("Project ${prj.path} already has a grgit property. Remove org.ajoberstar.grgit from either ${prj.path} or ${project.path}.")
          }
          prj.extensions.add(Grgit, 'grgit', grgit)
        }
      } catch (Exception e) {
        project.logger.debug("Failed trying to find git repository for ${project.path}", e)
        project.ext.grgit = null
      }
    }
  }
}
