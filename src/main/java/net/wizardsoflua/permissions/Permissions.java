package net.wizardsoflua.permissions;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOps;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.world.dimension.DimensionType;

public class Permissions {

  private final MinecraftServer server;

  public Permissions(MinecraftServer server) {
    this.server = server;
  }

  public boolean hasOperatorPrivileges(UUID playerId) {
    if (server.isSinglePlayer()
        && server.getWorld(DimensionType.OVERWORLD).getWorldInfo().areCommandsAllowed()) {
      return true;
    }
    EntityPlayerMP player = server.getPlayerList().getPlayerByUUID(playerId);
    if (player == null) {
      return false;
    }
    UserListOps oppedPlayers = server.getPlayerList().getOppedPlayers();
    UserListOpsEntry entry = oppedPlayers.getEntry(player.getGameProfile());
    return entry != null;
  }
}
