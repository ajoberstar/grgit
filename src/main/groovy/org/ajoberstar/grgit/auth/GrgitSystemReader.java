package org.ajoberstar.grgit.auth;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.SystemReader;
import org.eclipse.jgit.util.time.MonotonicClock;

public class GrgitSystemReader extends SystemReader {
  private static final Pattern PATH_SPLITTER = Pattern.compile(Pattern.quote(File.pathSeparator));
  private final SystemReader delegate;
  private final String gitSsh;

  public GrgitSystemReader(SystemReader delegate, String gitSsh) {
    this.delegate = delegate;
    this.gitSsh = gitSsh;
  }

  @Override
  public String getHostname() {
    return delegate.getHostname();
  }

  @Override
  public String getenv(String variable) {
    String value = delegate.getenv(variable);
    if ("GIT_SSH".equals(variable) && value == null) {
      return gitSsh;
    } else {
      return value;
    }
  }

  @Override
  public String getProperty(String key) {
    return delegate.getProperty(key);
  }

  @Override
  public FileBasedConfig openUserConfig(Config parent, FS fs) {
    return delegate.openUserConfig(parent, fs);
  }

  @Override
  public FileBasedConfig openSystemConfig(Config parent, FS fs) {
    return delegate.openSystemConfig(parent, fs);
  }

  @Override
  public long getCurrentTime() {
    return delegate.getCurrentTime();
  }

  @Override
  public MonotonicClock getClock() {
    return delegate.getClock();
  }

  @Override
  public int getTimezone(long when) {
    return delegate.getTimezone(when);
  }

  @Override
  public TimeZone getTimeZone() {
    return delegate.getTimeZone();
  }

  @Override
  public Locale getLocale() {
    return delegate.getLocale();
  }

  @Override
  public SimpleDateFormat getSimpleDateFormat(String pattern) {
    return delegate.getSimpleDateFormat(pattern);
  }

  @Override
  public SimpleDateFormat getSimpleDateFormat(String pattern, Locale locale) {
    return delegate.getSimpleDateFormat(pattern, locale);
  }

  @Override
  public DateFormat getDateTimeInstance(int dateStyle, int timeStyle) {
    return delegate.getDateTimeInstance(dateStyle, timeStyle);
  }

  @Override
  public boolean isWindows() {
    return delegate.isWindows();
  }

  @Override
  public boolean isMacOS() {
    return delegate.isWindows();
  }

  @Override
  public void checkPath(String path) throws CorruptObjectException {
    delegate.checkPath(path);
  }

  @Override
  public void checkPath(byte[] path) throws CorruptObjectException {
    delegate.checkPath(path);
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
