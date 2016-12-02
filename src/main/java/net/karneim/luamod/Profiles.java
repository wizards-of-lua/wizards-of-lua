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

  public @Nullable URL getStartupProfile() {
    return getProfile(STARTUP);
  }

  public void setStartupProfile(@Nullable URL url) {
    setProfile(STARTUP, url);
  }

  public @Nullable URL getDefaultProfile() {
    return getProfile(DEFAULT);
  }

  public void setDefaultProfile(@Nullable URL url) {
    setProfile(DEFAULT, url);
  }

  public @Nullable URL getUserProfile(@Nullable Entity owner) {
    if (owner == null) {
      return null;
    }
    return getProfile(owner.getUniqueID().toString());
  }

  public void setUserProfile(Entity owner, @Nullable URL url) {
    setProfile(owner.getUniqueID().toString(), url);
  }

  private @Nullable URL getProfile(String key) {
    String urlStr = profileMap.get(key);
    if (urlStr == null) {
      urlStr = config.getStringOrNull(PROFILE, key);
      profileMap.put(key, urlStr);
    }
    return toUrl(urlStr);
  }

  private void setProfile(String key, @Nullable URL url) {
    String urlStr = toString(url);
    if (urlStr == null) {
      profileMap.remove(key);
    } else {
      profileMap.put(key, urlStr);
    }
    config.setString(PROFILE, key, urlStr);
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
