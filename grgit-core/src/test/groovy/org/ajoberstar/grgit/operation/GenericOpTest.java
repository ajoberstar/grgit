package org.ajoberstar.grgit.operation;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.ajoberstar.grgit.Grgit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GenericOpTest {
  @Rule
  public TemporaryFolder tempDir = new TemporaryFolder();

  @Test
  public void consumerOperationWorks() throws IOException {
    File dir = tempDir.newFolder();
    Grgit grgit = Grgit.init(op -> {
      op.setDir(dir);
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
