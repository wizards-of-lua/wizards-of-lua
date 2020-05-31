package net.wizardsoflua.file;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.crypto.BadPaddingException;
import javax.inject.Inject;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.config.WolConfig;
import net.wizardsoflua.extension.api.inject.PostConstruct;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.server.api.ServerScoped;

// FIXME Adrodoc 28.04.2019: This class seems to implement multiple unrelated features
@ServerScoped
public class LuaFileRepository {
  @Resource
  private WizardsOfLua wol;
  @Resource
  private MinecraftServer server;
  @Inject
  private WolConfig config;

  private final Crypto crypto = new Crypto();
  private SpellPackFactory spellPackFactory;

  @PostConstruct
  private void postConstruct() {
    spellPackFactory = new SpellPackFactory(wol.getTempDir(), config.getSharedLibDir().toPath());
  }

  public List<String> getLuaFilenames(EntityPlayer player) {
    try {
      Path playerLibDir = config.getOrCreateWizardConfig(player.getUniqueID()).getLibDir().toPath();
      try (Stream<Path> files = Files.walk(playerLibDir, FileVisitOption.FOLLOW_LINKS)) {
        return files.filter(p -> !Files.isDirectory(p)).filter(p -> !isHidden(p))
            .map(p -> playerLibDir.relativize(p).toString()).collect(Collectors.toList());
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public List<String> getSharedLuaFilenames() {
    try {
      Path sharedLibDir = config.getSharedLibDir().toPath();
      try (Stream<Path> files = Files.walk(sharedLibDir, FileVisitOption.FOLLOW_LINKS)) {
        return files.filter(p -> !Files.isDirectory(p)).filter(p -> !isHidden(p))
            .map(p -> sharedLibDir.relativize(p).toString()).collect(Collectors.toList());
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public List<String> getToplevelSharedDirectoryNames() {
    try {
      Path sharedLibDir = config.getSharedLibDir().toPath();
      try (Stream<Path> files = Files.walk(sharedLibDir, 1, FileVisitOption.FOLLOW_LINKS)) {
        return files.filter(p -> Files.isDirectory(p)).filter(p -> !isHidden(p))
            .map(p -> sharedLibDir.relativize(p).toString()).collect(Collectors.toList());
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
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

  public URL getFileEditURL(UUID playerUuid, String context, String filepath) {
    if (filepath.contains("..") || filepath.startsWith("/") || filepath.startsWith("\\")) {
      throw new IllegalArgumentException(String.format("Illegal path '%s'", filepath));
    }
    String fileReference = getFileReferenceFor(playerUuid, context, filepath);
    String hostname = config.getRestApiConfig().getHostname();
    String protocol = config.getRestApiConfig().getProtocol();
    int port = config.getRestApiConfig().getPort();

    try {
      URL result = new URL(protocol + "://" + hostname + ":" + port + "/wol/lua/" + fileReference);
      return result;
    } catch (MalformedURLException e) {
      throw new UncheckedIOException(e);
    }
  }

  public URL getFileEditURL(EntityPlayer player, String filepath) throws IllegalArgumentException {
    return getFileEditURL(player.getUniqueID(), "private", filepath);
  }

  public URL getSharedFileEditURL(String filepath) throws IllegalArgumentException {
    return getFileEditURL(null, "shared", filepath);
  }

  public URL getSpellPackExportURL(String filepath) {
    if (filepath.contains("..") || filepath.startsWith("/") || filepath.startsWith("\\")) {
      throw new IllegalArgumentException(String.format("Illegal path '%s'", filepath));
    }
    File file = new File(config.getSharedLibDir(), filepath);

    if (!file.exists()) {
      throw new IllegalArgumentException(
          "Can't export " + filepath + "! Directory does not exist!");
    }
    if (!file.isDirectory()) {
      throw new IllegalArgumentException("Can't export " + filepath + "! It's not a directory!");
    }
    String hostname = config.getRestApiConfig().getHostname();
    String protocol = config.getRestApiConfig().getProtocol();
    int port = config.getRestApiConfig().getPort();

    String fileReference = getSpellPackFileReferenceFor(filepath);
    try {
      URL result =
          new URL(protocol + "://" + hostname + ":" + port + "/wol/export/" + fileReference);
      return result;
    } catch (MalformedURLException e) {
      throw new UncheckedIOException(e);
    }
  }

  private String getSpellPackFileReferenceFor(String filepath) {
    if (filepath.contains(File.separator)) {
      throw new IllegalArgumentException(
          "Spell packs can only be generated from a top level directory inside the shared library folder!");
    }
    return "shared" + "/" + filepath + ".jar";
  }

  public void deleteFile(String fileReference) {
    try {
      File file = getFile(fileReference);
      Files.delete(file.toPath());
      Path parent = file.getParentFile().toPath();
      if (isEmpty(parent)) {
        Files.delete(parent);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void deleteFile(EntityPlayer player, String filepath) throws IllegalArgumentException {
    try {
      if (filepath.contains("..") || filepath.startsWith("/") || filepath.startsWith("\\")) {
        throw new IllegalArgumentException(String.format("Illegal path '%s'", filepath));
      }
      UUID playerId = player.getUniqueID();
      File file = new File(config.getOrCreateWizardConfig(playerId).getLibDir(), filepath);
      Files.delete(file.toPath());
      Path parent = file.getParentFile().toPath();
      if (isEmpty(parent)) {
        Files.delete(parent);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void deleteSharedFile(String filepath) throws IllegalArgumentException {
    try {
      if (filepath.contains("..") || filepath.startsWith("/") || filepath.startsWith("\\")) {
        throw new IllegalArgumentException(String.format("Illegal path '%s'", filepath));
      }
      File file = new File(config.getSharedLibDir(), filepath);
      Files.delete(file.toPath());
      Path parent = file.getParentFile().toPath();
      if (isEmpty(parent)) {
        Files.delete(parent);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void move(String fileReference, UUID playerUuid, String context, String newFilepath) {
    try {
      if (newFilepath.contains("..") || newFilepath.startsWith("/")
          || newFilepath.startsWith("\\")) {
        throw new IllegalArgumentException(String.format("Illegal path '%s'", newFilepath));
      }
      String oldFilepath = getFilepathFor(fileReference);
      File oldFile = getFile(fileReference);

      Path contextDir = getContextDir(playerUuid, context);
      File newFile = contextDir.resolve(newFilepath).toFile();

      if (!oldFile.exists()) {
        throw new IllegalArgumentException(
            "Can't move " + oldFilepath + " to " + newFilepath + "! Source file does not exist!");
      }
      if (newFile.exists()) {
        throw new IllegalArgumentException(
            "Can't move " + oldFilepath + " to " + newFilepath + "! Target file already exists!");
      }
      Files.createDirectories(newFile.getParentFile().toPath());
      Files.move(oldFile.toPath(), newFile.toPath());
      Path parent = oldFile.getParentFile().toPath();
      if (isEmpty(parent)) {
        Files.delete(parent);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void move(UUID playerUuid, String fromContext, String fromPath, String toContext,
      String toPath) {
    try {
      if (fromPath.contains("..") || fromPath.startsWith("/") || fromPath.startsWith("\\")) {
        throw new IllegalArgumentException(String.format("Illegal path '%s'", fromPath));
      }
      if (toPath.contains("..") || toPath.startsWith("/") || toPath.startsWith("\\")) {
        throw new IllegalArgumentException(String.format("Illegal path '%s'", toPath));
      }
      File oldFile = getFile(playerUuid, fromContext, fromPath);
      File newFile = getFile(playerUuid, toContext, toPath);

      if (!oldFile.exists()) {
        throw new IllegalArgumentException("Can't move [" + fromContext + "] " + fromPath + " to ["
            + toContext + "] " + toPath + "! Source file does not exist!");
      }
      if (newFile.exists()) {
        throw new IllegalArgumentException("Can't move [" + fromContext + "] " + fromPath + " to ["
            + toContext + "] " + toPath + "! Target file already exists!");
      }
      Files.createDirectories(newFile.getParentFile().toPath());
      Files.move(oldFile.toPath(), newFile.toPath());
      Path parent = oldFile.getParentFile().toPath();
      if (isEmpty(parent)) {
        Files.delete(parent);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
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
      File oldFile = new File(config.getOrCreateWizardConfig(playerId).getLibDir(), filepath);
      File newFile = new File(config.getOrCreateWizardConfig(playerId).getLibDir(), newFilepath);
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
      throw new UncheckedIOException(e);
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
      File oldFile = new File(config.getSharedLibDir(), filepath);
      File newFile = new File(config.getSharedLibDir(), newFilepath);
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
      throw new UncheckedIOException(e);
    }
  }

  public LuaFile loadLuaFile(String fileReference) {
    try {
      File file = getFile(fileReference);
      String name = file.getName();
      String content;
      boolean exists;
      LocalDateTime lastModified;
      if (file.exists()) {
        Charset cs = Charset.defaultCharset();
        content = new String(Files.readAllBytes(file.toPath()), cs);
        exists = true;
        lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(file.toPath()).toInstant(),
            ZoneId.systemDefault());
      } else {
        content = null;
        exists = false;
        lastModified = null;
      }
      String context = getContextFrom(fileReference);
      return new LuaFile(getFilepathFor(fileReference), name, context, fileReference, content,
          exists, lastModified);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
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
      throw new UncheckedIOException(e);
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
      throw new UncheckedIOException(e);
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

  public boolean isDirectory(String fileReference) {
    File file = getFile(fileReference);
    return file.isDirectory();
  }


  public boolean exists(UUID playerUuid, String context, String filepath) {
    File f = getFile(playerUuid, context, filepath);
    return f.exists();
  }

  public URL getPasswordTokenUrl(EntityPlayer player) {
    String hostname = config.getRestApiConfig().getHostname();
    String protocol = config.getRestApiConfig().getProtocol();
    int port = config.getRestApiConfig().getPort();
    String uuid = player.getUniqueID().toString();
    String token = getLoginToken(player);
    try {
      URL result =
          new URL(protocol + "://" + hostname + ":" + port + "/wol/login/" + uuid + "/" + token);
      return result;
    } catch (MalformedURLException e) {
      throw new UncheckedIOException(e);
    }
  }

  public boolean isValidLoginToken(UUID playerId, String token) {
    if (!hasOperatorPrivileges(playerId)) {
      return false;
    }
    int index = token.indexOf('/');
    String encryptedPlayerPassword = token.substring(index + 1);
    String serverPassword = config.getRestApiConfig().getApiKey();

    try {
      String playerPassword = crypto.decrypt(playerId, serverPassword, encryptedPlayerPassword);
      String expectedPlayerPassword = config.getOrCreateWizardConfig(playerId).getRestApiKey();
      return expectedPlayerPassword.equals(playerPassword);
    } catch (BadPaddingException e) {
      return false;
    }
  }

  private boolean hasOperatorPrivileges(UUID playerId) {
    PlayerProfileCache cache = server.getPlayerProfileCache();
    GameProfile gameProfile = cache.getProfileByUUID(playerId);
    return server.getPlayerList().canSendCommands(gameProfile);
  }

  private String getLoginToken(EntityPlayer player) {
    return getLoginToken(player.getUniqueID());
  }

  private String getLoginToken(UUID playerId) {
    String playerPassword = config.getOrCreateWizardConfig(playerId).getRestApiKey();
    String serverPassword = config.getRestApiConfig().getApiKey();
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
      return new File(config.getSharedLibDir(), filepath);
    } else {
      UUID ownerId = getOwnerIdFor(fileReference);
      return new File(config.getOrCreateWizardConfig(ownerId).getLibDir(), filepath);
    }
  }

  public String getContextFrom(String fileReference) {
    String filepath = getFilepathFor(fileReference);
    if (filepath.contains("..")) {
      throw new IllegalArgumentException("Filepath must not contain '..' elements!");
    }
    if (fileReference.startsWith("shared/")) {
      return "shared";
    } else if (fileReference.startsWith("world/")) {
      return "world";
    } else {
      getOwnerIdFor(fileReference);
      return "private";
    }
  }

  private File getFile(UUID playerUuid, String context, String filepath) {
    Path contextDir = getContextDir(playerUuid, context);
    if (contextDir != null) {
      return new File(contextDir.toFile(), filepath);
    }
    throw new IllegalArgumentException("Unsupported context: '" + context + "'");
  }

  private String getFileReferenceFor(UUID playerUuid, String context, String filepath) {
    switch (context) {
      case "shared":
        return getSharedFileReferenceFor(filepath);
      case "private":
        return getFileReferenceFor(playerUuid, filepath);
      case "world":
        // TODO
      default:
        throw new IllegalArgumentException("Unsupported context: '" + context + "'");
    }
  }

  public String getFileReferenceFor(EntityPlayer player, String filepath) {
    return getFileReferenceFor(player.getUniqueID(), filepath);
  }

  public String getFileReferenceFor(UUID playerUuid, String filepath) {
    return playerUuid.toString() + "/" + filepath.replace('\\', '/');
  }

  public String getSharedFileReferenceFor(String filepath) {
    return "shared" + "/" + filepath.replace('\\', '/');
  }

  public String getFilepathFor(String fileReference) {
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

  public List<String> getMatches(UUID playerUuid, String context, String filepath) {
    if (filepath.contains("..") || filepath.startsWith("/") || filepath.startsWith("\\")) {
      return Collections.emptyList();
    }
    Path contextDir = getContextDir(playerUuid, context);
    if (contextDir != null) {
      return getMatches(contextDir, filepath);
    }

    // TODO "world"
    return Collections.emptyList();
  }

  private Path getContextDir(UUID playerUuid, String context) {
    switch (context) {
      case "shared":
        return config.getSharedLibDir().toPath();
      case "private":
        return config.getOrCreateWizardConfig(playerUuid).getLibDir().toPath();
      case "world":
        // TODO "world"
      default:
        return null;
    }
  }

  private List<String> getMatches(Path contextDir, String pathStr) {
    try {
      List<String> result = new ArrayList<>();
      Path path = contextDir.resolve(pathStr);
      Path relPath = contextDir.relativize(path);
      for (Path elem : relPath) {
        if (elem.startsWith(".")) {
          return result;
        }
      }
      if (Files.exists(path) && !contextDir.equals(path)) {
        if (Files.isDirectory(path) && !pathStr.endsWith("/")) {
          result.add(relPath.toString() + "/");
        }
      }
      while (true) {
        if (Files.exists(path) && Files.isDirectory(path)) {
          List<String> entries = getChildren(contextDir, path).stream()
              .filter(s -> s.startsWith(pathStr) && !s.equals(pathStr))
              .collect(Collectors.toList());
          result.addAll(entries);
          return result;
        }
        if (contextDir.equals(path)) {
          break;
        }
        path = path.getParent();
      }
      return result;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private List<String> getChildren(Path path) throws IOException {
    return getChildren(path, path);
  }

  private List<String> getChildren(Path relativeDir, Path path) throws IOException {
    List<String> entries = Files.list(path) //
        .filter(s -> !s.getFileName().toString().startsWith(".")) //
        .map(p -> { //
          if (Files.isDirectory(p)) {
            return relativeDir.relativize(p).toString() + "/";
          } else {
            return relativeDir.relativize(p).toString();
          }
        }).collect(Collectors.toList());
    return entries;
  }

  public Directory loadDirectory(UUID playerUuid, String context, String filepath)
      throws IOException {
    Path contextDir = getContextDir(playerUuid, context);
    Path fullpath = contextDir.resolve(filepath);
    if (!Files.isDirectory(fullpath)) {
      throw new IllegalArgumentException("File '" + filepath + "' is not a directory.");
    }
    List<String> children = getChildren(fullpath);
    return new Directory(filepath, fullpath.getFileName().toString(), children);
  }

}
