package net.karneim.luamod;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.karneim.luamod.config.ModConfiguration;
import net.minecraft.entity.Entity;

public class ProfileUrls {
  private static final String PROFILE = "profile";
  private static final String DEFAULT = "default";

  private final ModConfiguration config;
  private final Map<String, String> profileMap = new HashMap<String, String>();

  public ProfileUrls(ModConfiguration config) {
    this.config = checkNotNull(config);
  }

  public @Nullable String getProfileUrl(@Nullable Entity owner) {
    if (owner == null) {
      return null;
    }
    String userId = owner.getUniqueID().toString();
    String result = profileMap.get(userId);
    if (result == null) {
      result = config.getStringOrNull(PROFILE, userId);
      profileMap.put(userId, result);
    }
    return result;
  }

  public void setProfileUrl(Entity owner, @Nullable String profileUrl) {
    String userId = owner.getUniqueID().toString();
    if (profileUrl == null) {
      profileMap.remove(userId);
    } else {
      profileMap.put(userId, profileUrl);
    }
    config.setString(PROFILE, userId, profileUrl);
    config.save();
  }

  public @Nullable String getDefaultProfileUrl() {
    String result = profileMap.get(DEFAULT);
    if (result == null) {
      result = config.getStringOrNull(PROFILE, DEFAULT);
      profileMap.put(DEFAULT, result);
    }
    return result;
  }

  public void setDefaultProfileUrl(@Nullable String profileUrl) {
    if (profileUrl == null) {
      profileMap.remove(DEFAULT);
    } else {
      profileMap.put(DEFAULT, profileUrl);
    }
    config.setString(PROFILE, DEFAULT, profileUrl);
    config.save();
  }

}
