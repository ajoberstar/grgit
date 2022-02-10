package org.ajoberstar.grgit.gradle;

import javax.inject.Inject;

import org.ajoberstar.grgit.Grgit;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.provider.Property;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;

public abstract class GrgitService implements BuildService<GrgitService.Params>, AutoCloseable {
  private static final Logger logger = Logging.getLogger(GrgitService.class);

  public interface Params extends BuildServiceParameters {
    DirectoryProperty getCurrentDirectory();

    DirectoryProperty getDirectory();

    Property<Boolean> getInitIfNotExists();
  }

  private final Grgit grgit;

  @Inject
  public GrgitService() {
    if (getParameters().getCurrentDirectory().isPresent()) {
      this.grgit = Grgit.open(op -> {
        op.setCurrentDir(getParameters().getCurrentDirectory().get().getAsFile());
      });
      return;
    }

    var dir = getParameters().getDirectory().get().getAsFile();
    if (dir.exists()) {
      this.grgit = Grgit.open(op -> {
        op.setDir(dir);
      });
    } else if (getParameters().getInitIfNotExists().get()) {
      this.grgit = Grgit.init(op -> {
        op.setDir(dir);
      });
    } else {
      throw new IllegalStateException("No Git repo exists at " + dir + " and initIfNotExists is false. Cannot proceed.");
    }
  }

  public Grgit getGrgit() {
    return grgit;
  }

  @Override
  public void close() throws Exception {
    logger.info("Closing Git repo: {}", grgit.getRepository().getRootDir());
    grgit.close();
  }
}
