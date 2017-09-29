package net.wizardsoflua.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModConfiguration {

  private static final String SHARED_HOME = "shared";
  private static final String SERVER_HOME = "server";

  public static ModConfiguration create(FMLPreInitializationEvent event, String configName) {
    File configDir = new File(event.getModConfigurationDirectory(), configName);
    File cfgFile = new File(configDir, configName + ".cfg");
    if (event.getSuggestedConfigurationFile().exists() && !cfgFile.exists()) {
      event.getSuggestedConfigurationFile().renameTo(cfgFile);
    }

    Configuration config = new Configuration(cfgFile);
    config.load();

    if (config.hasKey("luatickslimit", "general")) {
      // Little fix because of bad config entry
      // See https://github.com/wizards-of-lua/wizards-of-lua/issues/51
      // TODO remove this when we left alpha phase
      config.removeCategory(config.getCategory("luatickslimit"));
    }
    return new ModConfiguration(configDir, config);
  }

  @SuppressWarnings("unused")
  private final File configDir;
  private final IntProperty luaTicksLimit;
  private final BooleanProperty shouldShowAboutMessage;
  private final File luaHomeDir;

  public ModConfiguration(File configDir, Configuration config) {
    this.configDir = checkNotNull(configDir, "configDir==null!");
    luaHomeDir = new File(configDir, "home");

    checkNotNull(config, "config==null!");
    luaTicksLimit = new IntProperty(config, "general", "luaTicksLimit", 10000, 1000, 10000000,
        "Max. number of Lua ticks a spell can run per game tick");
    shouldShowAboutMessage = new BooleanProperty(config, "general", "showAboutMessage", true,
        "Shows the about message to the player at first login");


    if (config.hasChanged()) {
      config.save();
    }
  }

  public File getLuaHomeDir(ICommandSender owner) {
    Entity entity = owner.getCommandSenderEntity();
    if (entity instanceof EntityPlayer) {
      String uuid = ((EntityPlayer) entity).getCachedUniqueIdString();
      return new File(luaHomeDir, uuid);
    }
    if (owner instanceof MinecraftServer) {
      return new File(luaHomeDir, SERVER_HOME);
    }
    return new File(luaHomeDir, SHARED_HOME);
  }

  public int getLuaTicksLimit() {
    return luaTicksLimit.getValue();
  }

  public int setLuaTicksLimit(int value) {
    return luaTicksLimit.setValue(value, true);
  }

  public boolean shouldShowAboutMessage() {
    return shouldShowAboutMessage.getValue();
  }

}
