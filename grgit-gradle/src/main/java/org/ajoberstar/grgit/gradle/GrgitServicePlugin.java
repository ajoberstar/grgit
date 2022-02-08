package org.ajoberstar.grgit.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;

public class GrgitServicePlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    GrgitServiceExtension extension = project.getExtensions().create("grgitService", GrgitServiceExtension.class);

    Provider<GrgitService> serviceProvider = project.getGradle().getSharedServices().registerIfAbsent("grgit", GrgitService.class, spec -> {
      spec.getParameters().getCurrentDirectory().set(project.getLayout().getProjectDirectory());
      spec.getParameters().getInitIfNotExists().set(false);
      spec.getMaxParallelUsages().set(1);
    });

    extension.getService().set(serviceProvider);
  }
}
