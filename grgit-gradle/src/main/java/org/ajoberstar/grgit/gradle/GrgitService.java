package org.ajoberstar.grgit.gradle;

import java.io.File;
import java.util.Optional;

import javax.inject.Inject;

import org.ajoberstar.grgit.Grgit;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;

public abstract class GrgitService implements BuildService<GrgitService.Params>, AutoCloseable {
  private static final Logger logger = Logging.getLogger(GrgitService.class);

  public interface Params extends BuildServiceParameters {
    DirectoryProperty getCurrentDirectory();

    DirectoryProperty getDirectory();

    Property<Boolean> getInitIfNotExists();
  }

  @Inject
  public GrgitService(ProviderFactory providers) {
    getGrgitProperty().set(providers.provider(this::makeGrgit));
    getGrgitProperty().disallowChanges();
    getGrgitProperty().finalizeValueOnRead();
  }

  protected abstract Property<Grgit> getGrgitProperty();

  public Grgit getGrgit() {
    return getGrgitProperty().get();
  }

  public Optional<Grgit> findGrgit() {
    try {
      return Optional.of(getGrgit());
    } catch (Exception e) {
      logger.info("Failed to make grgit service.", e);
      return Optional.empty();
    }
  }

  @Override
  public void close() throws Exception {
    findGrgit().ifPresent(grgit -> {
      logger.info("Closing Git repo: {}", grgit.getRepository().getRootDir());
      grgit.close();
    });
  }

  private Grgit makeGrgit() {
    if (getParameters().getCurrentDirectory().isPresent()) {
      return Grgit.open(op -> {
        op.setCurrentDir(getParameters().getCurrentDirectory().get().getAsFile());
      });
    }

    var dir = getParameters().getDirectory().get().getAsFile();
    var gitDir = new File(dir, ".git");
    if (gitDir.exists()) {
      return Grgit.open(op -> {
        op.setDir(dir);
      });
    } else if (getParameters().getInitIfNotExists().getOrElse(false)) {
      return Grgit.init(op -> {
        op.setDir(dir);
      });
    } else {
      throw new IllegalStateException("No Git repo exists at " + dir + " and initIfNotExists is false. Cannot proceed.");
    }
  }
}
