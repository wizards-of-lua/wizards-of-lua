package net.wizardsoflua;

import static java.util.Objects.requireNonNull;
import java.util.UUID;
import javax.annotation.Nullable;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.server.api.ServerScoped;

@ServerScoped
public class GameProfiles {
  @Resource
  private MinecraftServer server;

  public @Nullable GameProfile getGameProfileByName(String name) {
    PlayerProfileCache cache = server.getPlayerProfileCache();
    return cache.getGameProfileForUsername(name);
  }

  public @Nullable GameProfile getGameProfileById(UUID uuid) {
    PlayerProfileCache cache = server.getPlayerProfileCache();
    return cache.getProfileByUUID(uuid);
  }

  public @Nullable GameProfile getGameProfile(String nameOrUuid) {
    requireNonNull(nameOrUuid, "nameOrUuid");
    UUID uuid;
    try {
      uuid = UUID.fromString(nameOrUuid);
    } catch (IllegalArgumentException e) {
      return getGameProfileByName(nameOrUuid);
    }
    return getGameProfileById(uuid);
  }

  public @Nullable UUID getUuid(String nameOrUuid) {
    requireNonNull(nameOrUuid, "nameOrUuid");
    try {
      return UUID.fromString(nameOrUuid);
    } catch (IllegalArgumentException e) {
      GameProfile profile = getGameProfileByName(nameOrUuid);
      if (profile != null) {
        return profile.getId();
      } else {
        return null;
      }
    }
  }
}
