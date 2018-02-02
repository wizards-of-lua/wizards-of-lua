package net.wizardsoflua.permissions;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOpsEntry;

public class Permissions {

  private final MinecraftServer server;

  public Permissions(MinecraftServer server) {
    this.server = server;
  }

  public boolean hasOperatorPrivileges(UUID playerId) {
    EntityPlayerMP player = server.getPlayerList().getPlayerByUUID(playerId);
    UserListOpsEntry entry = (UserListOpsEntry) server.getPlayerList().getOppedPlayers()
        .getEntry(player.getGameProfile());
    return entry != null;
  }
}
