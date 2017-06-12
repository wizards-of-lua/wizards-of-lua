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
import net.wizardsoflua.lua.LuaCommand;
import net.wizardsoflua.lua.SpellProgramFactory;
import net.wizardsoflua.lua.scheduling.SpellSchedulingConfig;
import net.wizardsoflua.lua.scheduling.SpellSchedulingContextFactory;
import net.wizardsoflua.spell.ChunkLoaderTicketSupport;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.spell.SpellEntityFactory;

@Mod(modid = WizardsOfLua.MODID, version = WizardsOfLua.VERSION, acceptableRemoteVersions = "*")
public class WizardsOfLua {
  public static final String MODID = "wol";
  public static final String VERSION = "0.0.0";

  private static final int DEFAULT_ALLOWANCE = 10000;
  private static final boolean DEFAULT_AUTO_SLEEP = true;

  @Instance(MODID)
  public static WizardsOfLua instance;

  public final Logger logger = LogManager.getLogger(WizardsOfLua.class.getName());
  private final SpellEntityFactory spellEntityFactory;
  private final SpellProgramFactory spellProgramFactory;
  private final SpellSchedulingContextFactory spellSchedulingContextFactory;

  private MinecraftServer server;

  public WizardsOfLua() {
    spellSchedulingContextFactory = new SpellSchedulingContextFactory(
        new SpellSchedulingConfig(DEFAULT_ALLOWANCE, DEFAULT_AUTO_SLEEP));
    spellProgramFactory = new SpellProgramFactory(spellSchedulingContextFactory);
    spellEntityFactory = new SpellEntityFactory(spellProgramFactory);
  }

  public SpellEntityFactory getSpellEntityFactory() {
    return spellEntityFactory;
  }

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
