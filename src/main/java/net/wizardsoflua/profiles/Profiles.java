package net.wizardsoflua.profiles;

import java.io.File;
import javax.annotation.Nullable;
import javax.inject.Inject;
import net.minecraft.entity.player.EntityPlayer;
import net.wizardsoflua.config.WolConfig;

public class Profiles {
  private static final String SHARED_PROFILE = "shared-profile";
  private static final String PROFILE = "profile";

  @Inject
  private WolConfig config;

  public @Nullable String getProfile(EntityPlayer player) {
    File dir = config.getOrCreateWizardConfig(player.getUniqueID()).getLibDir();
    File profile = new File(dir, PROFILE + ".lua");
    if (profile.exists()) {
      return PROFILE;
    }
    return null;
  }

  public @Nullable String getSharedProfile() {
    File dir = config.getGeneralConfig().getSharedLibDir();
    File sharedProfile = new File(dir, SHARED_PROFILE + ".lua");
    if (sharedProfile.exists()) {
      return SHARED_PROFILE;
    }
    return null;
  }
}
