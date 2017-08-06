package net.wizardsoflua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.time.Clock;

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
import net.wizardsoflua.spell.ChunkLoaderTicketSupport;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.spell.SpellEntityFactory;

@Mod(modid = WizardsOfLua.MODID, version = WizardsOfLua.VERSION, acceptableRemoteVersions = "*")
public class WizardsOfLua {
  public static final String MODID = "wol";
  public static final String VERSION = "1.0.0";

  private static final int DEFAULT_ALLOWANCE = 10000;
  private static final boolean DEFAULT_AUTO_SLEEP = true;

  @Instance(MODID)
  public static WizardsOfLua instance;

  public final Logger logger = LogManager.getLogger(WizardsOfLua.class.getName());
  private final SpellEntityFactory spellEntityFactory;
  private final SpellProgramFactory spellProgramFactory;

  private MinecraftServer server;
  /**
   * Clock used for RuntimeModule
   */
  private Clock clock = getDefaultClock();

  public WizardsOfLua() {
    spellProgramFactory = new SpellProgramFactory(new SpellProgramFactory.Context() {
      @Override
      public Clock getClock() {
        return clock;
      }
    });
    spellEntityFactory = new SpellEntityFactory(spellProgramFactory);
  }

  public SpellEntityFactory getSpellEntityFactory() {
    return spellEntityFactory;
  }

  public Clock getClock() {
    return clock;
  }

  public void setClock(Clock clock) {
    this.clock = clock;
  }
  
  public Clock getDefaultClock() {
    return Clock.systemDefaultZone();
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
