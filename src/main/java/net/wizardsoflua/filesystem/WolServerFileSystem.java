package net.wizardsoflua.filesystem;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import net.minecraft.server.MinecraftServer;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.server.api.ServerScoped;

@ServerScoped
public class WolServerFileSystem extends RestrictedFileSystem {
  public WolServerFileSystem(@Resource MinecraftServer server) {
    super(FileSystems.getDefault(), getWorldFolder(server));
  }

  private static Path getWorldFolder(MinecraftServer server) {
    return server.getDataDirectory().toPath().normalize().toAbsolutePath()
        .resolve(server.getFolderName());
  }
}
