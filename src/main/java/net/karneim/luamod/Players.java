package net.karneim.luamod;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class Players {
  private final MinecraftServer server;

  public Players(MinecraftServer server) {
    this.server = server;
  }

  public String[] list() {
    return server.getAllUsernames();
  }

  public EntityPlayerMP getPlayer(String name) {
    return server.getPlayerList().getPlayerByUsername(name);
  }

}
