package net.karneim.luamod.cache;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import net.karneim.luamod.gist.GistFileRef;

public class FileCache {

  private final File directory;
  // TODO remove this. no need to cache that since this cache is always empty when used.
  private final Map<GistFileRef, String> contents = new HashMap<GistFileRef, String>();

  public FileCache(File directory) {
    this.directory = directory;
    this.directory.mkdirs();
  }

  public void clear() throws IOException {
    FileUtils.deleteDirectory(directory);
    directory.mkdirs();
    contents.clear();
  }

  public @Nullable String load(GistFileRef ref) throws IOException {
    String result = contents.get(ref);
    if (result == null) {
      File file = new File(directory, ref.asFilename());
      if (file.exists() && file.canRead()) {
        FileReader reader = new FileReader(file);
        result = IOUtils.toString(reader);
        contents.put(ref, result);
      }
    }
    return result;
  }

  public void save(GistFileRef ref, String content) throws IOException {
    File file = new File(directory, ref.asFilename());
    file.getParentFile().mkdirs();
    FileWriter writer = new FileWriter(file);
    IOUtils.write(content, writer);
    writer.close();
    contents.put(ref, content);
  }

}
