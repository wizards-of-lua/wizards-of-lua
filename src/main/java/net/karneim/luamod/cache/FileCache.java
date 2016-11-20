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

public class FileCache {

  private final File directory;
  // TODO expire the cache contents
  private final Map<String,String> contents = new HashMap<String,String>();

  public FileCache(File directory) {
    this.directory = directory;
  }

  public void clear() throws IOException {
    FileUtils.deleteDirectory(directory);
    directory.mkdirs();
    contents.clear();
  }

  public @Nullable String load(String id) throws IOException {
    String result = contents.get(id);
    if ( result == null) {
      File file = new File(directory, id);
      if (file.exists() && file.canRead()) {
        FileReader reader = new FileReader(file);
        result = IOUtils.toString(reader);
        contents.put(id, result);
      }
    }
    return result;
  }

  public void save(String id, String content) throws IOException {
    File file = new File(directory, id);
    FileWriter writer = new FileWriter(file);
    IOUtils.write(content, writer);
    writer.close();
    contents.put(id, content);
  }

}
