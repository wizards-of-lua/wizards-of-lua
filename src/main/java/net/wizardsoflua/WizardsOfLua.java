package net.wizardsoflua;

import java.io.File;
import java.time.Clock;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.CheckResult;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
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

@Mod(modid = WizardsOfLua.MODID, version = WizardsOfLua.VERSION, acceptableRemoteVersions = "*",
    updateJSON = "https://raw.githubusercontent.com/wizards-of-lua/wizards-of-lua/master/versions.json")
public class WizardsOfLua {
  public static final String MODID = "wol";
  public static final String NAME = "Wizards of Lua";
  public static final String CONFIG_NAME = "wizards-of-lua";
  public static final String VERSION = "@MOD_VERSION@";
  public static final String URL = "http://www.wizards-of-lua.net";

  @Instance(MODID)
  public static WizardsOfLua instance;

  public final Logger logger = LogManager.getLogger(WizardsOfLua.class.getName());
  private final SpellRegistry spellRegistry = new SpellRegistry();

  // TODO move these lazy instances into a new state class
  private ModConfiguration config;
  private AboutMessage aboutMessage;
  private SpellEntityFactory spellEntityFactory;
  private SpellProgramFactory spellProgramFactory;

  // private MinecraftServer server;

  /**
   * Clock used for RuntimeModule
   */
  private Clock clock = getDefaultClock();

  public WizardsOfLua() {}

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    config = ModConfiguration.create(event, CONFIG_NAME);
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

      @Override
      public @Nullable String getRecommendedVersion() {
        String result = null;
        for (ModContainer mod : Loader.instance().getModList()) {
          if (mod.getModId().equals(MODID)) {
            CheckResult checkResult = ForgeVersion.getResult(mod);
            Status status = checkResult.status;
            if (status == Status.OUTDATED || status == Status.BETA_OUTDATED) {
              result = checkResult.target.toString();
            }
          }
        }
        return result;
      }
    });
    spellProgramFactory = new SpellProgramFactory(new SpellProgramFactory.Context() {
      @Override
      public Clock getClock() {
        return clock;
      }

      @Override
      public int getLuaTicksLimit() {
        return config.getLuaTicksLimit();
      }

      @Override
      public File getLuaHomeDir(ICommandSender owner) {
        return config.getLuaHomeDir(owner);
      }
      
    });
    spellEntityFactory = new SpellEntityFactory(spellRegistry, spellProgramFactory);
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

  public ModConfiguration getConfig() {
    return config;
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

}
