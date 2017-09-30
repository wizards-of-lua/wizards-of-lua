package net.wizardsoflua.profiles;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.wizardsoflua.config.ModConfiguration;

public class Profiles {

  public interface Context {

    ModConfiguration getConfig();
  }

  private final Context context;

  public Profiles(Context context) {
    this.context = checkNotNull(context, "context==null!");
  }

  public void setProfile(EntityPlayer player, @Nullable String module) {
    context.getConfig().getUserConfig(player).setProfile(module);
  }

  public @Nullable String getProfile(EntityPlayer player) {
    return context.getConfig().getUserConfig(player).getProfile();
  }

}
