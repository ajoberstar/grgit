package org.ajoberstar.grgit.operation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.ajoberstar.grgit.Grgit;

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
