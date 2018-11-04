package net.wizardsoflua.file;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.crypto.BadPaddingException;

import net.minecraft.entity.player.EntityPlayer;
import net.wizardsoflua.config.RestApiConfig;

public class LuaFileRepository {

  public interface Context {
    File getPlayerLibDir(UUID playerId);

    File getSharedLibDir();

    String getPlayerRestApiKey(UUID playerId);

    RestApiConfig getRestApiConfig();

    boolean isOperator(UUID playerId);

    Path getTempDir();
  }

  private final Crypto crypto = new Crypto();
  private final Context context;
  private final SpellPackFactory spellPackFactory;

  public LuaFileRepository(Context context) {
    this.context = context;
    spellPackFactory =
        new SpellPackFactory(context.getTempDir(), context.getSharedLibDir().toPath());
  }

  public List<String> getLuaFilenames(EntityPlayer player) {
    try {
      Path playerLibDir = context.getPlayerLibDir(player.getUniqueID()).toPath();
      try (Stream<Path> files = Files.walk(playerLibDir, FileVisitOption.FOLLOW_LINKS)) {
        return files.filter(p -> !Files.isDirectory(p)).filter(p -> !isHidden(p))
            .map(p -> playerLibDir.relativize(p).toString()).collect(Collectors.toList());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<String> getSharedLuaFilenames() {
    try {
      Path sharedLibDir = context.getSharedLibDir().toPath();
      try (Stream<Path> files = Files.walk(sharedLibDir, FileVisitOption.FOLLOW_LINKS)) {
        return files.filter(p -> !Files.isDirectory(p)).filter(p -> !isHidden(p))
            .map(p -> sharedLibDir.relativize(p).toString()).collect(Collectors.toList());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<String> getToplevelSharedDirectoryNames() {
    try {
      Path sharedLibDir = context.getSharedLibDir().toPath();
      try (Stream<Path> files = Files.walk(sharedLibDir, 1, FileVisitOption.FOLLOW_LINKS)) {
        return files.filter(p -> Files.isDirectory(p)).filter(p -> !isHidden(p))
            .map(p -> sharedLibDir.relativize(p).toString()).collect(Collectors.toList());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isHidden(Path path) {
    for (Path elem : path) {
      if (elem.getFileName().toString().startsWith(".")) {
        return true;
      }
    }
    return false;
  }

  public URL getFileEditURL(EntityPlayer player, String filepath) throws IllegalArgumentException {
    if (filepath.contains("..") || filepath.startsWith("/") || filepath.startsWith("\\")) {
      throw new IllegalArgumentException(String.format("Illegal path '%s'", filepath));
    }
    String hostname = context.getRestApiConfig().getHostname();
    String protocol = context.getRestApiConfig().getProtocol();
    int port = context.getRestApiConfig().getPort();

    String fileReference = getFileReferenceFor(player, filepath);
    try {
      URL result = new URL(protocol + "://" + hostname + ":" + port + "/wol/lua/" + fileReference);
      return result;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public URL getSharedFileEditURL(String filepath) throws IllegalArgumentException {
    if (filepath.contains("..") || filepath.startsWith("/") || filepath.startsWith("\\")) {
      throw new IllegalArgumentException(String.format("Illegal path '%s'", filepath));
    }
    String hostname = context.getRestApiConfig().getHostname();
    String protocol = context.getRestApiConfig().getProtocol();
    int port = context.getRestApiConfig().getPort();

    String fileReference = getSharedFileReferenceFor(filepath);
    try {
      URL result = new URL(protocol + "://" + hostname + ":" + port + "/wol/lua/" + fileReference);
      return result;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public URL getSpellPackExportURL(String filepath) {
    if (filepath.contains("..") || filepath.startsWith("/") || filepath.startsWith("\\")) {
      throw new IllegalArgumentException(String.format("Illegal path '%s'", filepath));
    }
    File file = new File(context.getSharedLibDir(), filepath);

    if (!file.exists()) {
      throw new IllegalArgumentException(
          "Can't export " + filepath + "! Directory does not exist!");
    }
    if (!file.isDirectory()) {
      throw new IllegalArgumentException("Can't export " + filepath + "! It's not a directory!");
    }
    String hostname = context.getRestApiConfig().getHostname();
    String protocol = context.getRestApiConfig().getProtocol();
    int port = context.getRestApiConfig().getPort();

    String fileReference = getSpellPackFileReferenceFor(filepath);
    try {
      URL result =
          new URL(protocol + "://" + hostname + ":" + port + "/wol/export/" + fileReference);
      return result;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private String getSpellPackFileReferenceFor(String filepath) {
    if (filepath.contains(File.separator)) {
      throw new IllegalArgumentException(
          "Spell packs can only be generated from a top level directory inside the shared library folder!");
    }
    return "shared" + "/" + filepath + ".jar";
  }

  public void deleteFile(EntityPlayer player, String filepath) throws IllegalArgumentException {
    try {
      if (filepath.contains("..") || filepath.startsWith("/") || filepath.startsWith("\\")) {
        throw new IllegalArgumentException(String.format("Illegal path '%s'", filepath));
      }
      UUID playerId = player.getUniqueID();
      File file = new File(context.getPlayerLibDir(playerId), filepath);
      Files.delete(file.toPath());
      Path parent = file.getParentFile().toPath();
      if (isEmpty(parent)) {
        Files.delete(parent);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void deleteSharedFile(String filepath) throws IllegalArgumentException {
    try {
      if (filepath.contains("..") || filepath.startsWith("/") || filepath.startsWith("\\")) {
        throw new IllegalArgumentException(String.format("Illegal path '%s'", filepath));
      }
      File file = new File(context.getSharedLibDir(), filepath);
      Files.delete(file.toPath());
      Path parent = file.getParentFile().toPath();
      if (isEmpty(parent)) {
        Files.delete(parent);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void moveFile(EntityPlayer player, String filepath, String newFilepath)
      throws IllegalArgumentException {
    try {
      if (filepath.contains("..") || filepath.startsWith("/") || filepath.startsWith("\\")) {
        throw new IllegalArgumentException(String.format("Illegal path '%s'", filepath));
      }
      if (newFilepath.contains("..") || newFilepath.startsWith("/")
          || newFilepath.startsWith("\\")) {
        throw new IllegalArgumentException(String.format("Illegal path '%s'", newFilepath));
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
      Files.createDirectories(newFile.getParentFile().toPath());
      Files.move(oldFile.toPath(), newFile.toPath());
      Path parent = oldFile.getParentFile().toPath();
      if (isEmpty(parent)) {
        Files.delete(parent);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void moveSharedFile(String filepath, String newFilepath) throws IllegalArgumentException {
    try {
      if (filepath.contains("..") || filepath.startsWith("/") || filepath.startsWith("\\")) {
        throw new IllegalArgumentException(String.format("Illegal path '%s'", filepath));
      }
      if (newFilepath.contains("..") || newFilepath.startsWith("/")
          || newFilepath.startsWith("\\")) {
        throw new IllegalArgumentException(String.format("Illegal path '%s'", newFilepath));
      }
      File oldFile = new File(context.getSharedLibDir(), filepath);
      File newFile = new File(context.getSharedLibDir(), newFilepath);
      if (!oldFile.exists()) {
        throw new IllegalArgumentException(
            "Can't move " + filepath + " to " + newFilepath + "! Source file does not exist!");
      }
      if (newFile.exists()) {
        throw new IllegalArgumentException(
            "Can't move " + filepath + " to " + newFilepath + "! Target file already exists!");
      }
      Files.createDirectories(newFile.getParentFile().toPath());
      Files.move(oldFile.toPath(), newFile.toPath());
      Path parent = oldFile.getParentFile().toPath();
      if (isEmpty(parent)) {
        Files.delete(parent);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public LuaFile loadLuaFile(String fileReference) {
    try {
      File file = getFile(fileReference);
      String name = file.getName();
      String content;
      if (file.exists()) {
        Charset cs = Charset.defaultCharset();
        content = new String(Files.readAllBytes(file.toPath()), cs);
      } else {
        content = "";
      }
      return new LuaFile(getFilepathFor(fileReference), name, fileReference, content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public SpellPack createSpellPack(String fileReference) {
    if (!fileReference.endsWith(".jar")) {
      throw new IllegalArgumentException(
          "Can't create spell pack! File reference must be a jar file.");
    }
    try {
      String name = fileReference.substring(0, fileReference.length() - 4);
      File file = getFile(name);
      if (!file.exists()) {
        throw new IllegalArgumentException(
            format("Can't create spell pack! File %s does not exist.", name));
      }
      if (!file.isDirectory()) {
        throw new IllegalArgumentException(
            format("Can't create spell pack! File %s is not a directory.", name));
      }
      return spellPackFactory.createSpellPack(fileReference, file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Saves the given content to the file denoted by the given file reference.
   * 
   * @param fileReference
   * @param content
   */
  public void saveLuaFile(String fileReference, String content) {
    try {
      File file = getFile(fileReference);
      if (!file.getParentFile().exists()) {
        Files.createDirectories(file.getParentFile().toPath());
      }
      Charset cs = Charset.defaultCharset();
      byte[] bytes = content.getBytes(cs.name());
      Path path = file.toPath();
      Files.write(path, bytes);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns true, if the file denoted by the given file reference exists.
   * 
   * @return true, if the file denoted by the given file reference exists
   */
  public boolean exists(String fileReference) {
    File file = getFile(fileReference);
    return file.exists();
  }

  public URL getPasswordTokenUrl(EntityPlayer player) {
    String hostname = context.getRestApiConfig().getHostname();
    String protocol = context.getRestApiConfig().getProtocol();
    int port = context.getRestApiConfig().getPort();
    String uuid = player.getUniqueID().toString();
    String token = getLoginToken(player);
    try {
      URL result =
          new URL(protocol + "://" + hostname + ":" + port + "/wol/login/" + uuid + "/" + token);
      return result;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean isValidLoginToken(UUID playerId, String token) {
    int index = token.indexOf('/');
    if (!context.isOperator(playerId)) {
      return false;
    }
    String encryptedPlayerPassword = token.substring(index + 1);
    String serverPassword = context.getRestApiConfig().getApiKey();

    try {
      String playerPassword = crypto.decrypt(playerId, serverPassword, encryptedPlayerPassword);
      String expectedPlayerPassword = context.getPlayerRestApiKey(playerId);
      return expectedPlayerPassword.equals(playerPassword);
    } catch (BadPaddingException e) {
      return false;
    }
  }

  private String getLoginToken(EntityPlayer player) {
    return getLoginToken(player.getUniqueID());
  }

  private String getLoginToken(UUID playerId) {
    String playerPassword = context.getPlayerRestApiKey(playerId);
    String serverPassword = context.getRestApiConfig().getApiKey();
    try {
      String encryptedPlayerPassword = crypto.encrypt(playerId, serverPassword, playerPassword);
      return encryptedPlayerPassword;
    } catch (BadPaddingException e) {
      throw new IllegalStateException(
          "Can't create login token. Perhaps the server's or the player's API key is corrupted?");
    }
  }

  private File getFile(String fileReference) {
    String filepath = getFilepathFor(fileReference);
    if (filepath.contains("..")) {
      throw new IllegalArgumentException("Filepath must not contain '..' elements!");
    }
    if (fileReference.startsWith("shared/")) {
      return new File(context.getSharedLibDir(), filepath);
    } else {
      UUID ownerId = getOwnerIdFor(fileReference);
      return new File(context.getPlayerLibDir(ownerId), filepath);
    }
  }

  public String getFileReferenceFor(EntityPlayer player, String filepath) {
    return player.getUniqueID().toString() + "/" + filepath.replace('\\', '/');
  }

  public String getSharedFileReferenceFor(String filepath) {
    return "shared" + "/" + filepath.replace('\\', '/');
  }

  private String getFilepathFor(String fileReference) {
    int index = fileReference.indexOf('/');
    return fileReference.substring(index + 1);
  }

  private UUID getOwnerIdFor(String fileReference) {
    int index = fileReference.indexOf('/');
    String playerIdStr = fileReference.substring(0, index);
    UUID result = UUID.fromString(playerIdStr);
    return result;
  }

  private boolean isEmpty(Path directory) throws IOException {
    try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
      return !dirStream.iterator().hasNext();
    }
  }

}
