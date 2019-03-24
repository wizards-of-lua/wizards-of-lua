package net.wizardsoflua;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.CheckResult;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.load.LoaderException;
import net.wizardsoflua.chunk.ChunkForceManager;
import net.wizardsoflua.config.GeneralConfig;
import net.wizardsoflua.config.RestApiConfig;
import net.wizardsoflua.config.WizardConfig;
import net.wizardsoflua.config.WolConfig;
import net.wizardsoflua.event.WolEventHandler;
import net.wizardsoflua.file.LuaFile;
import net.wizardsoflua.file.LuaFileRepository;
import net.wizardsoflua.file.SpellPack;
import net.wizardsoflua.filesystem.RestrictedFileSystem;
import net.wizardsoflua.gist.GistRepo;
import net.wizardsoflua.lua.ExtensionLoader;
import net.wizardsoflua.lua.LuaCommand;
import net.wizardsoflua.lua.SpellProgramFactory;
import net.wizardsoflua.lua.extension.InjectionScope;
import net.wizardsoflua.lua.module.searcher.LuaFunctionBinaryCache;
import net.wizardsoflua.permissions.Permissions;
import net.wizardsoflua.profiles.Profiles;
import net.wizardsoflua.rest.WolRestApiServer;
import net.wizardsoflua.spell.SpellEntityFactory;
import net.wizardsoflua.spell.SpellRegistry;
import net.wizardsoflua.startup.Startup;
import net.wizardsoflua.wol.WolCommand;

@Mod(WizardsOfLua.MODID)
public class WizardsOfLua {
  public static final String MODID = "wol";
  public static final String NAME = "Wizards of Lua";
  public static final String CONFIG_NAME = "wizards-of-lua";
  public static final String VERSION = "@MOD_VERSION@";
  public static final String URL = "http://www.wizards-of-lua.net";

  // TODO do we need this anymore?
  public static WizardsOfLua instance;

  public final Logger logger = LogManager.getLogger();

  private final SpellRegistry spellRegistry = new SpellRegistry();
  private final LuaFunctionBinaryCache luaFunctionCache = new LuaFunctionBinaryCache();
  private final GistRepo gistRepo = new GistRepo();

  // TODO move these lazy instances into a new state class
  private Path tempDir;
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
  private FileSystem worldFileSystem;
  private ChunkForceManager chunkForceManager;

  /**
   * Clock used for RuntimeModule
   */
  private Clock clock = getDefaultClock();
  private InjectionScope rootScope = new InjectionScope();

  public WizardsOfLua() {
    instance = this;
    registerEventHandlers();

  }

  /**
   * This registers this mod to both event bus instances.
   *
   * @see net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD
   * @see net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE
   */
  private void registerEventHandlers() {
    FMLJavaModLoadingContext.get().getModEventBus().register(new ModSpecificEventBusHandling());
    MinecraftForge.EVENT_BUS.register(new MainForgeEventBusListener());
  }

  private class ModSpecificEventBusHandling {

    @SubscribeEvent
    public void onFmlCommonSetup(FMLCommonSetupEvent event) {
      ExtensionLoader.initialize(logger);
      try {
        tempDir = Files.createTempDirectory("wizards-of-lua");
        config = WolConfig.create(CONFIG_NAME);
      } catch (IOException | LoaderException | CallException | CallPausedException
          | InterruptedException e1) {
        throw new RuntimeException(e1);
      }
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
        public @Nullable String getRecommendedVersion() {
          String result = null;
          IModInfo modInfo = ModList.get().getModFileById(MODID).getMods().get(0);
          CheckResult checkResult = VersionChecker.getResult(modInfo);
          VersionChecker.Status status = checkResult.status;
          if (status == VersionChecker.Status.OUTDATED
              || status == VersionChecker.Status.BETA_OUTDATED) {
            result = checkResult.target.toString();
          }
          return result;
        }
      });
      spellProgramFactory = new SpellProgramFactory(logger, new SpellProgramFactory.Context() {
        @Override
        public Clock getClock() {
          return clock;
        }

        @Override
        public long getLuaTicksLimit() {
          return getConfig().getGeneralConfig().getLuaTicksLimit();
        }

        @Override
        public long getEventListenerLuaTicksLimit() {
          return config.getGeneralConfig().getEventListenerLuaTicksLimit();
        }

        @Override
        public @Nullable String getLuaPathElementOfPlayer(String nameOrUuid) {
          UUID uuid = getUUID(nameOrUuid);
          return getConfig().getOrCreateWizardConfig(uuid).getLibDirPathElement();
        }

        private UUID getUUID(String nameOrUuid) {
          try {
            return UUID.fromString(nameOrUuid);
          } catch (IllegalArgumentException e) {
            GameProfile profile = gameProfiles.getGameProfileByName(nameOrUuid);
            if (profile != null) {
              return profile.getId();
            } else {
              throw new IllegalArgumentException(
                  format("Player not found with name '%s'", nameOrUuid));
            }
          }
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
        public Path getScriptDir() {
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
        public InjectionScope getRootScope() {
          return rootScope;
        }

        @Override
        public FileSystem getWorldFileSystem() {
          return WizardsOfLua.this.getWorldFileSystem();
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
      eventHandler = new WolEventHandler(() -> spellRegistry.getAll());
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

        @Override
        public Path getTempDir() {
          return tempDir;
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

        @Override
        public SpellPack createSpellPackByReference(String fileReference) {
          return getFileRepository().createSpellPack(fileReference);
        }

      });
      startup = new Startup(new Startup.Context() {
        @Override
        public Path getSharedLibDir() {
          return getConfig().getSharedLibDir().toPath();
        }

        @Override
        public MinecraftServer getServer() {
          return server;
        }

        @Override
        public Logger getLogger() {
          return logger;
        }

        @Override
        public SpellEntityFactory getSpellEntityFactory() {
          return spellEntityFactory;
        }
      });
    }

    @SubscribeEvent
    public void onFmlDedicatedServerSetup(FMLDedicatedServerSetupEvent event) {
      logger.info("Initializing Wizards-of-Lua, Version " + VERSION);
      MinecraftForge.EVENT_BUS.register(getSpellRegistry());
      MinecraftForge.EVENT_BUS.register(aboutMessage);
      MinecraftForge.EVENT_BUS.register(eventHandler);
    }
  }

  private class MainForgeEventBusListener {
    @SubscribeEvent
    public void onFmlServerStarting(FMLServerStartingEvent event) throws IOException {
      server = event.getServer();
      worldFileSystem = createWorldFileSystem(server.getDataDirectory(), server.getFolderName());
      chunkForceManager = new ChunkForceManager();
      gameProfiles = new GameProfiles(server);
      permissions = new Permissions(server);

      CommandDispatcher<CommandSource> cmdDispatcher = event.getCommandDispatcher();
      WolCommand.register(cmdDispatcher, WizardsOfLua.this);
      LuaCommand.register(cmdDispatcher, WizardsOfLua.this);

      restApiServer.start();
    }

    @SubscribeEvent
    public void onFmlServerStarted(FMLServerStartedEvent event) {
      logger.info(aboutMessage);
      runStartupSequence(server.getCommandSource());
    }

    @SubscribeEvent
    public void onFmlServerStopping(FMLServerStoppingEvent event) {
      restApiServer.stop();
    }

  }


  private FileSystem createWorldFileSystem(File serverDir, String worldFolderName) {
    Path worldDirectory =
        new File(serverDir, worldFolderName).toPath().normalize().toAbsolutePath();
    return new RestrictedFileSystem(FileSystems.getDefault(), worldDirectory);
  }


  public void runStartupSequence(CommandSource source) {
    startup.runStartupSequence(source);
  }

  public WolConfig getConfig() {
    return checkNotNull(config, "config==null!");
  }

  public Profiles getProfiles() {
    return checkNotNull(profiles, "profiles==null!");
  }

  public SpellEntityFactory getSpellEntityFactory() {
    return checkNotNull(spellEntityFactory, "spellEntityFactory==null!");
  }

  public SpellRegistry getSpellRegistry() {
    return checkNotNull(spellRegistry, "spellRegistry==null!");
  }

  public Clock getClock() {
    return clock;
  }

  public void setClock(Clock clock) {
    this.clock = checkNotNull(clock, "clock==null!");
  }

  public Clock getDefaultClock() {
    return Clock.systemDefaultZone();
  }

  public void clearLuaFunctionCache() {
    luaFunctionCache.clear();
  }

  public LuaFileRepository getFileRepository() {
    return checkNotNull(fileRepository, "fileRepository==null!");
  }

  public WolRestApiServer getRestServer() {
    return checkNotNull(restApiServer, "restApiServer==null!");
  }

  public GistRepo getGistRepo() {
    return checkNotNull(gistRepo, "gistRepo==null!");
  }

  public FileSystem getWorldFileSystem() {
    return checkNotNull(worldFileSystem, "worldFileSystem==null!");
  }

  public Permissions getPermissions() {
    return checkNotNull(permissions, "permissions==null!");
  }

  public ChunkForceManager getChunkForceManager() {
    return checkNotNull(chunkForceManager, "chunkForceManager == null!");
  }

}
