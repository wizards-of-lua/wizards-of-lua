package net.wizardsoflua;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Logger;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
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
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.wizardsoflua.config.GeneralConfig;
import net.wizardsoflua.config.RestApiConfig;
import net.wizardsoflua.config.WizardConfig;
import net.wizardsoflua.config.WolConfig;
import net.wizardsoflua.event.CustomLuaEvent;
import net.wizardsoflua.event.WolEventHandler;
import net.wizardsoflua.file.LuaFile;
import net.wizardsoflua.file.LuaFileRepository;
import net.wizardsoflua.gist.GistRepo;
import net.wizardsoflua.lua.LuaCommand;
import net.wizardsoflua.lua.SpellProgramFactory;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.module.searcher.LuaFunctionBinaryCache;
import net.wizardsoflua.permissions.Permissions;
import net.wizardsoflua.profiles.Profiles;
import net.wizardsoflua.rest.WolRestApiServer;
import net.wizardsoflua.spell.ChunkLoaderTicketSupport;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.spell.SpellEntityFactory;
import net.wizardsoflua.spell.SpellRegistry;
import net.wizardsoflua.startup.Startup;
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

  // public final Logger logger = LogManager.getLogger(WizardsOfLua.class.getName());
  public Logger logger;

  private final SpellRegistry spellRegistry = new SpellRegistry();
  private final LuaFunctionBinaryCache luaFunctionCache = new LuaFunctionBinaryCache();
  private final GistRepo gistRepo = new GistRepo();

  // TODO move these lazy instances into a new state class
  private WolConfig config;
  private AboutMessage aboutMessage;
  private WolEventHandler eventHandler;
  private SpellEntityFactory spellEntityFactory;
  private SpellProgramFactory spellProgramFactory;
  private Profiles profiles;
  private LuaFileRepository fileRepository;
  private WolRestApiServer restApiServer;

  private MinecraftServer server;
  private GameProfiles gameProfiles;
  private Startup startup;
  private Permissions permissions;

  /**
   * Clock used for RuntimeModule
   */
  private Clock clock = getDefaultClock();

  public WizardsOfLua() {}

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) throws Exception {
    logger = event.getModLog();
    config = WolConfig.create(event, CONFIG_NAME);
    aboutMessage = new AboutMessage(new AboutMessage.Context() {

      @Override
      public boolean shouldShowAboutMessage() {
        return getConfig().getGeneralConfig().isShowAboutMessage();
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
        return getConfig().getGeneralConfig().getLuaTicksLimit();
      }

      @Override
      public int getEventListenerLuaTicksLimit() {
        return config.getGeneralConfig().getEventListenerLuaTicksLimit();
      }

      @Override
      public @Nullable String getLuaPathElementOfPlayer(String nameOrUuid) {
        GameProfile profile = gameProfiles.getGameProfile(nameOrUuid);
        if (profile == null) {
          throw new IllegalArgumentException(
              format("Player not found with name or uuid '%s'", nameOrUuid));
        }
        return getConfig().getOrCreateWizardConfig(profile.getId()).getLibDirPathElement();
      }

      @Override
      public String getSharedLuaPath() {
        return getConfig().getSharedLuaPath();
      }

      @Override
      public Profiles getProfiles() {
        return profiles;
      }

      @Override
      public LuaFunctionBinaryCache getLuaFunctionBinaryCache() {
        return luaFunctionCache;
      }

      @Override
      public boolean isScriptGatewayEnabled() {
        return getConfig().getScriptGatewayConfig().isEnabled();
      }

      @Override
      public File getScriptDir() {
        return getConfig().getScriptGatewayConfig().getDir();
      }

      @Override
      public long getScriptTimeoutMillis() {
        return getConfig().getScriptGatewayConfig().getTimeoutMillis();
      }

      @Override
      public SpellRegistry getSpellRegistry() {
        return WizardsOfLua.this.getSpellRegistry();
      }

      @Override
      public String getWolVersion() {
        return WizardsOfLua.VERSION;
      }

    });
    spellEntityFactory = new SpellEntityFactory(spellRegistry, spellProgramFactory);
    profiles = new Profiles(new Profiles.Context() {

      @Override
      public GeneralConfig getGeneralConfig() {
        return getConfig().getGeneralConfig();
      }

      @Override
      public WizardConfig getWizardConfig(EntityPlayer player) {
        return getConfig().getOrCreateWizardConfig(player.getUniqueID());
      }

    });
    eventHandler = new WolEventHandler(new WolEventHandler.Context() {
      @Override
      public Iterable<SpellEntity> getSpells() {
        return spellRegistry.getAll();
      }

      @Override
      public boolean isSupportedLuaEvent(Event event) {
        return event instanceof CustomLuaEvent || LuaClassLoader.isSupported(event.getClass());
      }

      @Override
      public String getEventName(Event event) {
        if (event instanceof CustomLuaEvent) {
          return ((CustomLuaEvent) event).getName();
        }
        return LuaClassLoader.getLuaClassNameOf(event.getClass());
      }
    });
    fileRepository = new LuaFileRepository(new LuaFileRepository.Context() {
      @Override
      public File getPlayerLibDir(UUID playerId) {
        return getConfig().getOrCreateWizardConfig(playerId).getLibDir();
      }

      @Override
      public RestApiConfig getRestApiConfig() {
        return getConfig().getRestApiConfig();
      }

      @Override
      public File getSharedLibDir() {
        return getConfig().getSharedLibDir();
      }

      @Override
      public String getPlayerRestApiKey(UUID playerId) {
        return getConfig().getOrCreateWizardConfig(playerId).getRestApiKey();
      }

      @Override
      public boolean isOperator(UUID playerId) {
        return permissions.hasOperatorPrivileges(playerId);
      }
    });

    restApiServer = new WolRestApiServer(new WolRestApiServer.Context() {
      @Override
      public LuaFile getLuaFileByReference(String fileReference) {
        return getFileRepository().loadLuaFile(fileReference);
      }

      @Override
      public RestApiConfig getRestApiConfig() {
        return getConfig().getRestApiConfig();
      }

      @Override
      public void saveLuaFileByReference(String fileReference, String content) {
        getFileRepository().saveLuaFile(fileReference, content);
      }

      @Override
      public boolean isValidLoginToken(UUID playerId, String token) {
        return getFileRepository().isValidLoginToken(playerId, token);
      }

    });
    startup = new Startup(new Startup.Context() {
      @Override
      public File getSharedLibDir() {
        return getConfig().getSharedLibDir();
      }

      @Override
      public MinecraftServer getMinecraftServer() {
        return WizardsOfLua.this.server;
      }
    });
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    logger.info("Initializing Wizards-of-Lua, Version " + VERSION);
    MinecraftForge.EVENT_BUS.register(getSpellRegistry());
    MinecraftForge.EVENT_BUS.register(aboutMessage);
    MinecraftForge.EVENT_BUS.register(eventHandler);

    // SpellEntity.register();
  }

  @EventHandler
  public void serverStarting(FMLServerStartingEvent event) throws IOException {
    server = event.getServer();
    gameProfiles = new GameProfiles(server);
    permissions = new Permissions(server);
    event.registerServerCommand(new WolCommand(this, this.logger));
    event.registerServerCommand(new LuaCommand());
    ChunkLoaderTicketSupport.enableTicketSupport(instance);
    restApiServer.start();
  }

  @EventHandler
  public void serverStopping(FMLServerStoppingEvent event) {
    restApiServer.stop();
  }

  @EventHandler
  public void serverStarted(FMLServerStartedEvent event) {
    logger.info(aboutMessage);
    startup.execute();
  }

  public WolConfig getConfig() {
    return config;
  }

  public Profiles getProfiles() {
    return profiles;
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

  public void clearLuaFunctionCache() {
    luaFunctionCache.clear();
  }

  public LuaFileRepository getFileRepository() {
    return fileRepository;
  }

  public WolRestApiServer getRestServer() {
    return restApiServer;
  }

  public GistRepo getGistRepo() {
    return gistRepo;
  }

}
