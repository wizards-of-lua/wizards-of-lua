package net.wizardsoflua;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.CheckResult;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.load.LoaderException;
import net.wizardsoflua.event.WolEventHandler;
import net.wizardsoflua.extension.api.resource.RealTime;
import net.wizardsoflua.gist.GistRepo;
import net.wizardsoflua.imc.TypedImc;
import net.wizardsoflua.imc.WizardsOfLuaConsumer;
import net.wizardsoflua.lua.LuaCommand;
import net.wizardsoflua.lua.extension.InjectionScope;
import net.wizardsoflua.permissions.Permissions;
import net.wizardsoflua.rest.WolRestApiServer;
import net.wizardsoflua.startup.Startup;
import net.wizardsoflua.wol.WolCommand;

@Mod(WizardsOfLua.MODID)
public class WizardsOfLua {
  public static final String MODID = "wol";
  public static final String NAME = "Wizards of Lua";
  public static final String CONFIG_NAME = "wizards-of-lua";
  public static final String VERSION = "@MOD_VERSION@";
  public static final String URL = "http://www.wizards-of-lua.net";
  public static final Logger LOGGER = LogManager.getLogger();

  private final GistRepo gistRepo = new GistRepo();

  // TODO move these lazy instances into a new state class
  private Path tempDir;
  private AboutMessage aboutMessage;
  private WolEventHandler eventHandler;
  private WolRestApiServer restApiServer;

  private Permissions permissions;

  /**
   * Clock used for RuntimeModule
   */
  private Clock clock = getDefaultClock();
  // TODO Adrodoc 28.04.2019: Check for thread safety of creating root scope in mod loading thread
  // and server scopes in server threads
  private final InjectionScope rootScope = new InjectionScope();

  public WizardsOfLua() {
    rootScope.registerResource(WizardsOfLua.class, this);
    rootScope.registerResource(RealTime.class, new RealTime() {
      @Override
      public Clock getClock() {
        return clock;
      }
    });
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
      LOGGER.info("Initializing Wizards-of-Lua, Version " + VERSION);
      try {
        tempDir = Files.createTempDirectory("wizards-of-lua");
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
      eventHandler = new WolEventHandler(() -> spellRegistry.getAll());
      MinecraftForge.EVENT_BUS.register(aboutMessage);
      MinecraftForge.EVENT_BUS.register(eventHandler);
    }

    @SubscribeEvent
    public void processImcMessages(InterModProcessEvent event) {
      TypedImc.getMessages(event, WizardsOfLuaConsumer.class).forEach(it -> {
        it.accept(WizardsOfLua.this);
      });
    }
  }

  private final Map<MinecraftServer, InjectionScope> serverScopes = new HashMap<>();

  private class MainForgeEventBusListener {
    @SubscribeEvent
    public void onFmlServerStarting(FMLServerStartingEvent event) throws IOException {
      InjectionScope serverScope =
          serverScopes.computeIfAbsent(event.getServer(), it -> createServerScope(it));

      WolServer wolServer = serverScope.getInstance(WolServer.class);
      CommandDispatcher<CommandSource> cmdDispatcher = event.getCommandDispatcher();
      WolCommand.register(cmdDispatcher, wolServer);
      LuaCommand.register(cmdDispatcher, wolServer);

      serverScope.getInstance(Startup.class); // Initialize Startup

      restApiServer = new WolRestApiServer();
      restApiServer.start();
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
      MinecraftServer server = event.getServer();
      InjectionScope serverScope = serverScopes.remove(server);
      if (serverScope != null) {
        serverScope.close();
      }
    }

    @SubscribeEvent
    public void onFmlServerStarted(FMLServerStartedEvent event) {
      LOGGER.info(aboutMessage);
    }

    @SubscribeEvent
    public void onFmlServerStopping(FMLServerStoppingEvent event) {
      restApiServer.stop();
    }
  }

  private InjectionScope createServerScope(MinecraftServer server) {
    InjectionScope result = rootScope.createSubScope(ServerScoped.class);
    result.registerResource(MinecraftServer.class, server);
    return result;
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

  public GistRepo getGistRepo() {
    return checkNotNull(gistRepo, "gistRepo==null!");
  }

  public Permissions getPermissions() {
    return checkNotNull(permissions, "permissions==null!");
  }

  public Path getTempDir() {
    return tempDir;
  }
}
