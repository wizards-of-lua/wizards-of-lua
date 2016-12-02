package net.karneim.luamod;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.mockito.cglib.proxy.UndeclaredThrowableException;

import net.karneim.luamod.config.ModConfiguration;
import net.minecraft.entity.Entity;

public class Profiles {
  private static final String PROFILE = "profile";
  private static final String DEFAULT = "default";

  private final ModConfiguration config;
  private final Map<String, String> profileMap = new HashMap<String, String>();

  public Profiles(ModConfiguration config) {
    this.config = checkNotNull(config);
  }

  public @Nullable URL getUserProfile(@Nullable Entity owner) {
    if (owner == null) {
      return null;
    }
    String userId = owner.getUniqueID().toString();
    String urlStr = profileMap.get(userId);
    if (urlStr == null) {
      urlStr = config.getStringOrNull(PROFILE, userId);
      profileMap.put(userId, urlStr);
    }
    return toUrl(urlStr);
  }

  public void setUserProfile(Entity owner, @Nullable URL url) {
    String userId = owner.getUniqueID().toString();
    String urlStr = toString(url);
    if (urlStr == null) {
      profileMap.remove(userId);
    } else {
      profileMap.put(userId, urlStr);
    }
    config.setString(PROFILE, userId, urlStr);
    config.save();
  }

  public @Nullable URL getDefaultProfile() {
    String urlStr = profileMap.get(DEFAULT);
    if (urlStr == null) {
      urlStr = config.getStringOrNull(PROFILE, DEFAULT);
      profileMap.put(DEFAULT, urlStr);
    }
    return toUrl(urlStr);
  }

  public void setDefaultProfile(@Nullable URL url) {
    String urlStr = toString(url);
    if (urlStr == null) {
      profileMap.remove(DEFAULT);
    } else {
      profileMap.put(DEFAULT, urlStr);
    }
    config.setString(PROFILE, DEFAULT, urlStr);
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
