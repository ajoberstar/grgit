package org.ajoberstar.grgit.gradle;

import org.gradle.api.provider.Provider;

public class GrgitServiceExtension {
  private final Provider<GrgitService> service;

  public GrgitServiceExtension(Provider<GrgitService> service) {
    this.service = service;
  }

  public Provider<GrgitService> getService() {
    return service;
  }
}
