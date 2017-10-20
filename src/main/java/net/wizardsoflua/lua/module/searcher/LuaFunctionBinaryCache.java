package net.wizardsoflua.lua.module.searcher;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;

import net.wizardsoflua.lua.compiler.LuaFunctionBinary;

public class LuaFunctionBinaryCache
    implements LuaFunctionBinaryByPathCache, LuaFunctionBinaryByUrlCache {

  private final Map<Object, LuaFunctionBinary> map = new HashMap<>();
  private final Map<Path, FileTime> cacheDates = new HashMap<>();

  public void clear() {
    map.clear();
    cacheDates.clear();
  }

  @Override
  public LuaFunctionBinary get(URL key) {
    return map.get(key);
  }

  @Override
  public void put(URL key, LuaFunctionBinary fn) {
    map.put(key, fn);
  }

  @Override
  public LuaFunctionBinary get(Path key) throws IOException {
    if (Files.isReadable(key)) {
      FileTime cacheDate = cacheDates.get(key);
      if (cacheDate != null) {
        FileTime lastModified = Files.getLastModifiedTime(key);
        if (cacheDate.compareTo(lastModified) == 0) {
          return map.get(key);
        }
      }
    }
    return null;
  }

  @Override
  public void put(Path key, LuaFunctionBinary fn) throws IOException {
    FileTime lastModified = Files.getLastModifiedTime(key);
    map.put(key, fn);
    cacheDates.put(key, lastModified);
  }

}
