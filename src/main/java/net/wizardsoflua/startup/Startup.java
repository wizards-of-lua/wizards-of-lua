package net.wizardsoflua.startup;

import java.io.File;

import net.minecraft.server.MinecraftServer;

public class Startup {

  public interface Context {

    File getSharedLibDir();

    MinecraftServer getMinecraftServer();
  }

  private final Context context;

  public Startup(Context context) {
    this.context = context;
  }

  public void execute() {
    File dir = context.getSharedLibDir();
    File startupFile = new File(dir, "startup.lua");
    if (startupFile.exists()) {
      MinecraftServer server = context.getMinecraftServer();
      server.getCommandManager().executeCommand(server, "/lua require('startup')");
    }
  }

}
