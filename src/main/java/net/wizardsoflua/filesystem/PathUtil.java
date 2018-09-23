package net.wizardsoflua.filesystem;

import java.nio.file.Path;

public class PathUtil {

  public static Path toPath(Path dir, String filename) {
    Path path = dir.resolve(filename);
    Path normPath = path.normalize();
//    if (!normPath.equals(path)) {
//      throw new IllegalArgumentException(
//          String.format("Unknown or illegal file path: %s", filename));
//    }
    if (!normPath.startsWith(dir)) {
      throw new IllegalArgumentException(
          String.format("Unknown or illegal file path: %s", filename));
    }
    return normPath;
  }

}
