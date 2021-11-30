package org.ajoberstar.grgit.operation;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.ajoberstar.grgit.Grgit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class GenericOpTest {
  @TempDir
  public File tempDir;

  @Test
  @DisplayName("consumer operation works")
  public void consumerOperationWorks() throws IOException {
    Grgit grgit = Grgit.init(op -> {
      op.setDir(tempDir);
    });
    grgit.add(op -> {
      op.setPatterns(new HashSet<>(Arrays.asList(".")));
    });
    grgit.commit(op -> {
      op.setMessage("First commit");
    });
    assertEquals(1, grgit.log().size());
    grgit.close();
  }
}
