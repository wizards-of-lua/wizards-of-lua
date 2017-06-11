package net.wizardsoflua;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.wizardsoflua.command.LuaCommand;
import net.wizardsoflua.spell.ChunkLoaderTicketSupport;
import net.wizardsoflua.spell.SpellEntity;

@Mod(modid = WizardsOfLua.MODID, version = WizardsOfLua.VERSION, acceptableRemoteVersions = "*")
public class WizardsOfLua {
  public static final String MODID = "wol";
  public static final String VERSION = "0.0.0";

  @Instance(MODID)
  public static WizardsOfLua instance;

  public final Logger logger = LogManager.getLogger(WizardsOfLua.class.getName());
  private MinecraftServer server;

  @EventHandler
  public void init(FMLInitializationEvent event) {
    logger.info("Initializing Wizards-of-Lua, Version " + VERSION);
    SpellEntity.register();
  }

  @EventHandler
  public void serverStarted(FMLServerStartedEvent event) {}

  @EventHandler
  public void serverLoad(FMLServerStartingEvent event) {
    server = checkNotNull(event.getServer());
    event.registerServerCommand(new LuaCommand());
    ChunkLoaderTicketSupport.enableTicketSupport(instance);
  }


}
