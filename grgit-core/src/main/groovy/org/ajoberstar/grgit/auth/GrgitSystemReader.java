package org.ajoberstar.grgit.auth;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jgit.util.SystemReader;

public class GrgitSystemReader extends SystemReader.Delegate {
  private static final Pattern PATH_SPLITTER = Pattern.compile(Pattern.quote(File.pathSeparator));
  private final String gitSsh;

  public GrgitSystemReader(SystemReader delegate, String gitSsh) {
    super(delegate);
    this.gitSsh = gitSsh;
  }

  @Override
  public String getenv(String variable) {
    String value = super.getenv(variable);
    if ("GIT_SSH".equals(variable) && value == null) {
      return gitSsh;
    } else {
      return value;
    }
  }

  public static void install() {
    SystemReader current = SystemReader.getInstance();

    String gitSsh = Stream.of("ssh", "plink")
        .map(GrgitSystemReader::findExecutable)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElse(null);

    SystemReader grgit = new GrgitSystemReader(current, gitSsh);
    SystemReader.setInstance(grgit);
  }

  private static Optional<String> findExecutable(String exe) {
    List<String> extensions = Optional.ofNullable(System.getenv("PATHEXT"))
        .map(PATH_SPLITTER::splitAsStream)
        .map(stream -> stream.collect(Collectors.toList()))
        .orElse(Collections.emptyList());

    Function<Path, Stream<Path>> getCandidatePaths = dir -> {
      // assume PATHEXT is only set on Windows
      if (extensions.isEmpty()) {
        return Stream.of(dir.resolve(exe));
      } else {
        return extensions.stream()
            .map(ext -> dir.resolve(exe + ext));
      }
    };

    return PATH_SPLITTER.splitAsStream(System.getenv("PATH"))
        .map(Paths::get)
        .flatMap(getCandidatePaths)
        .filter(Files::isExecutable)
        .map(Path::toAbsolutePath)
        .map(Path::toString)
        .findFirst();
  }
}
