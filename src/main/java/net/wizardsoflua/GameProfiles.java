package net.wizardsoflua;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.UsernameCache;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.server.api.ServerScoped;

@ServerScoped
public class GameProfiles {
  @Resource
  private MinecraftServer server;

  public @Nullable GameProfile getGameProfile(String nameOrUuid) {
    checkNotNull(nameOrUuid, "nameOrUuid==null!");
    try {
      return getGameProfileById(UUID.fromString(nameOrUuid));
    } catch (IllegalArgumentException e) {
      return getGameProfileByName(nameOrUuid);
    }
  }

  public GameProfile getGameProfileByName(String playerName) {
    // TODO optimize performance
    Map<UUID, String> map = UsernameCache.getMap();
    for (Map.Entry<UUID, String> entry : map.entrySet()) {
      if (entry.getValue().equals(playerName)) {
        return server.getPlayerProfileCache().getProfileByUUID(entry.getKey());
      }
    }
    return null;
  }

  public GameProfile getGameProfileById(UUID uuid) {
    return server.getPlayerProfileCache().getProfileByUUID(uuid);
  }
}
