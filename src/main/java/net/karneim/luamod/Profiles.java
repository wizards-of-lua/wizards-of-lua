package net.karneim.luamod;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.karneim.luamod.config.ModConfiguration;
import net.minecraft.entity.Entity;

public class Profiles {
  private static final String PROFILE = "profile";
  private static final String DEFAULT = "default";
  private static final String STARTUP = "startup";

  private final ModConfiguration config;
  private final Map<String, String> profileMap = new HashMap<String, String>();

  public Profiles(ModConfiguration config) {
    this.config = checkNotNull(config);
  }

  public @Nullable String getStartupProfile() {
    return getProfile(STARTUP);
  }

  public void setStartupProfile(@Nullable String string) {
    setProfile(STARTUP, string);
  }

  public @Nullable String getDefaultProfile() {
    return getProfile(DEFAULT);
  }

  public void setDefaultProfile(@Nullable String string) {
    setProfile(DEFAULT, string);
  }

  public @Nullable String getUserProfile(@Nullable Entity owner) {
    if (owner == null) {
      return null;
    }
    return getProfile(owner.getUniqueID().toString());
  }

  public void setUserProfile(Entity owner, @Nullable String string) {
    setProfile(owner.getUniqueID().toString(), string);
  }

  private @Nullable String getProfile(String key) {
    String result = profileMap.get(key);
    if (result == null) {
      result = config.getStringOrNull(PROFILE, key);
      profileMap.put(key, result);
    }
    return result;
  }

  private void setProfile(String key, @Nullable String string) {
    if (string == null) {
      profileMap.remove(key);
    } else {
      profileMap.put(key, string);
    }
    config.setString(PROFILE, key, string);
    config.save();
  }

  private @Nullable URL toUrl(@Nullable String text) {
    try {
      return text == null ? null : new URL(text);
    } catch (MalformedURLException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  private @Nullable String toString(@Nullable URL url) {
    return url == null ? null : url.toExternalForm();
  }
}
