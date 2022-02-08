package org.ajoberstar.grgit.gradle;

import javax.inject.Inject;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

public class GrgitServiceExtension {
  private Property<GrgitService> service;

  @Inject
  public GrgitServiceExtension(ObjectFactory objectFactory) {
    this.service = objectFactory.property(GrgitService.class);
  }

  public Property<GrgitService> getService() {
    return service;
  }
}
