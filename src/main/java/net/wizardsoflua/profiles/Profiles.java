package net.wizardsoflua.profiles;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.wizardsoflua.config.WizardConfig;

public class Profiles {

  public interface Context {

    WizardConfig getWizardConfig(EntityPlayer player);
  }

  private final Context context;

  public Profiles(Context context) {
    this.context = checkNotNull(context, "context==null!");
  }

  public void setProfile(EntityPlayer player, @Nullable String module) {
    context.getWizardConfig(player).setAutoRequire(module);
  }

  public @Nullable String getProfile(EntityPlayer player) {
    return context.getWizardConfig(player).getAutoRequire();
  }

}
