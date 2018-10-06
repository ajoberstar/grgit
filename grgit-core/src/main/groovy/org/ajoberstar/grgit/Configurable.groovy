package org.ajoberstar.grgit

import org.ajoberstar.grgit.internal.AnnotateAtRuntime

@FunctionalInterface
@AnnotateAtRuntime(annotations = "org.gradle.api.HasImplicitReceiver")
interface Configurable<T> {
  void configure(T t)
}
