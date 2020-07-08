package org.ajoberstar.grgit.gradle

import org.ajoberstar.grgit.Grgit
import org.gradle.api.provider.Provider

public class GrgitExtension {
    public final Provider<GrgitBuildService> grgitBuildServiceProvider;

    public GrgitExtension(Provider<GrgitBuildService> grgitBuildServiceProvider) {
        this.grgitBuildServiceProvider = grgitBuildServiceProvider
    }

    @Delegate
    public Grgit lookup() {
        return grgitBuildServiceProvider.get().grgit;
    }
}
