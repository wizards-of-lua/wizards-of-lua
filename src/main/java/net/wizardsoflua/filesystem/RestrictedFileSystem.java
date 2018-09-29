package net.wizardsoflua.filesystem;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class RestrictedFileSystem extends DelegatingFileSystem {
  private Path topmostDirectory;

  public RestrictedFileSystem(FileSystem delegate, Path topmostDirectory) {
    super(delegate);
    this.topmostDirectory = checkNotNull(topmostDirectory, "topmostDirectory==null!");
  }

  @Override
  public Path getPath(String first, String... more) {
    if (first.startsWith("/")) {
      first = first.substring(1);
    }
    List<String> parts = Lists.asList(first, more);
    String filename = Joiner.on(File.separatorChar).join(parts);
    Path result = PathUtil.toPath(topmostDirectory, filename);
    return result;
  }
}
