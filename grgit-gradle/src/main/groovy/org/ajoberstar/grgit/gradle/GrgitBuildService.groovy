package org.ajoberstar.grgit.gradle

import org.ajoberstar.grgit.Grgit
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

abstract class GrgitBuildService implements BuildService<GrgitBuildService.Params>, AutoCloseable {

    private static final Logger LOGGER = Logging.getLogger(GrgitBuildService.class);

    interface Params extends BuildServiceParameters {
        DirectoryProperty getRootDirectory();
    }

    Grgit grgit;

    GrgitBuildService() {
        try {
            grgit = Grgit.open(currentDir: parameters.rootDirectory.get())
        } catch (Exception e) {
            LOGGER.debug("Failed trying to find git repository for ${parameters.rootDirectory.get()}", e)
            grgit = null
        }
    }

    @Delegate
    public Grgit lookup() {
        return grgit;
    }

    @Override
    public void close() throws Exception {
        if (grgit != null) {
            LOGGER.info("Closing Git repo: ${grgit.repository.rootDir}")
            grgit.close()
        }
    }
}
