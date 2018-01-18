package net.wizardsoflua.profiles;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.wizardsoflua.config.GeneralConfig;
import net.wizardsoflua.config.WizardConfig;

public class Profiles {

  private static final String SHARED_PROFILE = "shared-profile";
  private static final String PROFILE = "profile";

  public interface Context {
    GeneralConfig getGeneralConfig();

    WizardConfig getWizardConfig(EntityPlayer player);
  }

  private final Context context;

  public Profiles(Context context) {
    this.context = checkNotNull(context, "context==null!");
  }

  public @Nullable String getProfile(EntityPlayer player) {
    File dir = context.getWizardConfig(player).getLibDir();
    File profile = new File(dir, PROFILE + ".lua");
    if (profile.exists()) {
      return PROFILE;
    }
    return null;
  }

  public @Nullable String getSharedProfile() {
    File dir = context.getGeneralConfig().getSharedLibDir();
    File sharedProfile = new File(dir, SHARED_PROFILE + ".lua");
    if (sharedProfile.exists()) {
      return SHARED_PROFILE;
    }
    return null;
  }

}
