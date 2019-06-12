package net.wizardsoflua.file;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {
  public static void deleteRecursivelyIfExists(Path path) throws IOException {
    if (java.nio.file.Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
      deleteRecursively(path);
    }
  }

  public static void deleteRecursively(Path path, FileVisitOption... options) throws IOException {
    java.nio.file.Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        java.nio.file.Files.delete(file);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        java.nio.file.Files.delete(dir);
        return FileVisitResult.CONTINUE;
      }
    });
  }
}
