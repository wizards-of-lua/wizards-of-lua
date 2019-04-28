package net.wizardsoflua.permissions;

import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOps;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.world.dimension.DimensionType;
import net.wizardsoflua.ServerScoped;
import net.wizardsoflua.extension.api.inject.Resource;

@ServerScoped
public class Permissions {
  @Resource
  private MinecraftServer server;

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
