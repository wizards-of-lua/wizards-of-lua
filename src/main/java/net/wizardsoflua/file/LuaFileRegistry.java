package net.wizardsoflua.file;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.entity.player.EntityPlayer;
import net.wizardsoflua.config.RestConfig;

public class LuaFileRegistry {

  public interface Context {
    File getPlayerLibDir(UUID playerId);

    RestConfig getRestConfig();
  }

  private Context context;

  public LuaFileRegistry(Context context) {
    this.context = context;
  }

  public List<String> getLuaFilenames(EntityPlayer player) {
    try {
      Path playerLibDir = Paths.get(context.getPlayerLibDir(player.getUniqueID()).toURI());
      try (Stream<Path> files = Files.walk(playerLibDir, FileVisitOption.FOLLOW_LINKS)) {
        return files.filter(p -> !Files.isDirectory(p))
            .map(p -> playerLibDir.relativize(p).toString()).collect(Collectors.toList());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public URL getFileEditURL(EntityPlayer player, String filepath) {
    if ( filepath.contains("..")) {
      throw new IllegalArgumentException("Relative path syntax is not allowed!");
    }
    String hostname = context.getRestConfig().getHostname();
    int port = context.getRestConfig().getPort();

    String fileReference = getFileReferenceFor(player, filepath);
    try {
      URL result = new URL("http://" + hostname + ":" + port + "/wol/lua/" + fileReference);
      return result;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public void deleteFile(EntityPlayer player, String filepath) {
    try {
      if ( filepath.contains("..")) {
        throw new IllegalArgumentException("Relative path syntax is not allowed!");
      }
      UUID playerId = player.getUniqueID();
      File file = new File(context.getPlayerLibDir(playerId), filepath);
      Files.delete(Paths.get(file.toURI()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void moveFile(EntityPlayer player, String filepath, String newFilepath) {
    try {
      if ( filepath.contains("..")) {
        throw new IllegalArgumentException("Relative path syntax is not allowed!");
      }
      if ( newFilepath.contains("..")) {
        throw new IllegalArgumentException("Relative path syntax is not allowed!");
      }
      UUID playerId = player.getUniqueID();
      File oldFile = new File(context.getPlayerLibDir(playerId), filepath);
      File newFile = new File(context.getPlayerLibDir(playerId), newFilepath);
      if (!oldFile.exists()) {
        throw new IllegalArgumentException(
            "Can't move " + filepath + " to " + newFilepath + "! Source file does not exist!");
      }
      if (newFile.exists()) {
        throw new IllegalArgumentException(
            "Can't move " + filepath + " to " + newFilepath + "! Target file already exists!");
      }
      Files.move(Paths.get(oldFile.toURI()), Paths.get(newFile.toURI()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public LuaFile loadLuaFile(String fileReference) {
    try {
      String filepath = getFilepathFor(fileReference);
      if ( filepath.contains("..")) {
        throw new IllegalArgumentException("Relative path syntax is not allowed!");
      }
      UUID playerId = getPlayerIdFor(fileReference);
      File file = new File(context.getPlayerLibDir(playerId), filepath);
      String name = file.getName();
      String content;
      if (file.exists()) {
        content = new String(Files.readAllBytes(Paths.get(file.toURI())));
      } else {
        content = "";
      }
      return new LuaFile(filepath, name, fileReference, content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void saveLuaFile(String fileReference, String content) {
    try {
      String filepath = getFilepathFor(fileReference);
      if ( filepath.contains("..")) {
        throw new IllegalArgumentException("Relative path syntax is not allowed!");
      }
      UUID playerId = getPlayerIdFor(fileReference);
      File file = new File(context.getPlayerLibDir(playerId), filepath);
      if (!file.getParentFile().exists()) {
        Files.createDirectories(Paths.get(file.getParentFile().toURI()));
      }
      byte[] bytes = content.getBytes();
      Files.write(Paths.get(file.toURI()), bytes);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String getFileReferenceFor(EntityPlayer player, String filepath) {
    return player.getUniqueID().toString() + "/" + filepath;
  }

  private String getFilepathFor(String fileReference) {
    int index = fileReference.indexOf('/');
    String result = fileReference.substring(index + 1);
    return result;
  }

  private UUID getPlayerIdFor(String fileReference) {
    int index = fileReference.indexOf('/');
    String playerIdStr = fileReference.substring(0, index);
    UUID result = UUID.fromString(playerIdStr);
    return result;
  }



}
