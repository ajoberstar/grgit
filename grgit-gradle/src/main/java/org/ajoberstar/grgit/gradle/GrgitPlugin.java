package org.ajoberstar.grgit.gradle;

import org.ajoberstar.grgit.Grgit;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;

/**
 * Plugin adding a {@code grgit} property to all projects that searches for a Git repo from the
 * project's directory.
 *
 * @since 2.0.0
 */
public class GrgitPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(GrgitServicePlugin.class);
    var serviceExtension = project.getExtensions().getByType(GrgitServiceExtension.class);
    try {
      project.getLogger().info("The org.ajoberstar.grgit plugin eagerly opens a Grgit instance. Use org.ajoberstar.grgit.service for better performance.");
      project.getExtensions().add(Grgit.class, "grgit", serviceExtension.getService().get().getGrgit());
    } catch (Exception e) {
      project.getLogger().debug("Failed to open Grgit instance", e);
      project.getExtensions().getExtraProperties().set("grgit", null);
    }
  }
}
