package org.ajoberstar.grgit;

import org.gradle.api.HasImplicitReceiver;

@FunctionalInterface
@HasImplicitReceiver
public interface Configurable<T> {
  void configure(T t);
}
