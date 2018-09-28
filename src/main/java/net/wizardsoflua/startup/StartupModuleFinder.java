package net.wizardsoflua.startup;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StartupModuleFinder {

  public List<String> findStartupModulesIn(Path dir) throws IOException {
    List<String> result = new ArrayList<>();
    List<Path> startupFiles = findFiles(dir, "startup.lua");
    for (Path startupFile : startupFiles) {
      if (Files.exists(startupFile)) {
        String module = toModule(dir, startupFile);
        result.add(module);
      }
    }
    return result;
  }

  private List<Path> findFiles(Path dir, String name) throws IOException {
    List<Path> dirs = new ArrayList<>();
    dirs.add(dir);
    return findFiles(name, dirs, new ArrayList<>());
  }

  private List<Path> findFiles(String name, List<Path> dirs, List<Path> result) throws IOException {
    if (!dirs.isEmpty()) {
      List<Path> nextDirs = new ArrayList<>();
      for (Path dir : dirs) {
        for (Path p : Files.list(dir).collect(Collectors.toList())) {
          if (Files.isDirectory(p)) {
            nextDirs.add(p);
          } else if (p.getFileName().toString().equals(name)) {
            result.add(p);
          }
        }
      }
      findFiles(name, nextDirs, result);
    }
    return result;
  }

  private String toModule(Path dir, Path modulePath) {
    Path relativeModulePath = dir.relativize(modulePath);
    if (relativeModulePath.getFileName().toString().endsWith(".lua")) {
      String result = relativeModulePath.toString();
      result = result.substring(0, result.length() - 4);
      result = result.replace(File.separator, ".");
      return result;
    } else {
      throw new IllegalArgumentException(
          format("Missing '.lua' extension on file %s", relativeModulePath));
    }
  }
}
