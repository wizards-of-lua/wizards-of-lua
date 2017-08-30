package net.wizardsoflua;

import java.time.Clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.wizardsoflua.config.ModConfiguration;
import net.wizardsoflua.lua.LuaCommand;
import net.wizardsoflua.lua.SpellProgramFactory;
import net.wizardsoflua.spell.ChunkLoaderTicketSupport;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.spell.SpellEntityFactory;
import net.wizardsoflua.spell.SpellRegistry;
import net.wizardsoflua.wol.WolCommand;

@Mod(modid = WizardsOfLua.MODID, version = WizardsOfLua.VERSION, acceptableRemoteVersions = "*")
public class WizardsOfLua {
  public static final String MODID = "wol";
  public static final String NAME = "Wizards of Lua";
  public static final String CONFIG_DIR = "wizards-of-lua";
  public static final String VERSION = "1.0.0-alpha";
  public static final String URL = "http://www.wizards-of-lua.net";

  private static final int DEFAULT_LUA_TICKS_LIMIT = 10000;

  @Instance(MODID)
  public static WizardsOfLua instance;

  public final Logger logger = LogManager.getLogger(WizardsOfLua.class.getName());
  private final ModConfiguration config = new ModConfiguration(CONFIG_DIR);
  private final AboutMessage aboutMessage;
  private final SpellRegistry spellRegistry = new SpellRegistry();
  private final SpellEntityFactory spellEntityFactory;
  private final SpellProgramFactory spellProgramFactory;

  private int luaTicksLimit = DEFAULT_LUA_TICKS_LIMIT;

  // private MinecraftServer server;

  /**
   * Clock used for RuntimeModule
   */
  private Clock clock = getDefaultClock();

  public WizardsOfLua() {
    aboutMessage = new AboutMessage(new AboutMessage.Context() {

      @Override
      public boolean shouldShowAboutMessage() {
        return config.shouldShowAboutMessage();
      }

      @Override
      public String getVersion() {
        return VERSION;
      }

      @Override
      public String getUrl() {
        return URL;
      }

      @Override
      public String getName() {
        return NAME;
      }
    });
    spellProgramFactory = new SpellProgramFactory(new SpellProgramFactory.Context() {
      @Override
      public Clock getClock() {
        return clock;
      }

      @Override
      public int getLuaTicksLimit() {
        return luaTicksLimit;
      }
    });
    spellEntityFactory = new SpellEntityFactory(spellRegistry, spellProgramFactory);
  }

  public SpellEntityFactory getSpellEntityFactory() {
    return spellEntityFactory;
  }

  public SpellRegistry getSpellRegistry() {
    return spellRegistry;
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
  public void preInit(FMLPreInitializationEvent event) {
    config.init(event);
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    logger.info("Initializing Wizards-of-Lua, Version " + VERSION);
    MinecraftForge.EVENT_BUS.register(getSpellRegistry());
    MinecraftForge.EVENT_BUS.register(aboutMessage);
    SpellEntity.register();
  }

  @EventHandler
  public void serverLoad(FMLServerStartingEvent event) {
    // server = checkNotNull(event.getServer());
    event.registerServerCommand(new WolCommand());
    event.registerServerCommand(new LuaCommand());
    ChunkLoaderTicketSupport.enableTicketSupport(instance);
  }

  @EventHandler
  public void serverStarted(FMLServerStartedEvent event) {
    logger.info(aboutMessage);
  }

}
