package net.karneim.luamod;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.karneim.luamod.cache.FileCache;
import net.karneim.luamod.cache.LuaFunctionBinaryCache;
import net.karneim.luamod.config.ModConfiguration;
import net.karneim.luamod.credentials.CredentialsStore;
import net.karneim.luamod.cursor.ClipboardRegistry;
import net.karneim.luamod.gist.GistRepo;
import net.karneim.luamod.lua.CommandAdmin;
import net.karneim.luamod.lua.CommandLua;
import net.karneim.luamod.lua.CommandMessagePatched;
import net.karneim.luamod.lua.Permissions;
import net.karneim.luamod.lua.SpellEntity;
import net.karneim.luamod.lua.SpellEntityFactory;
import net.karneim.luamod.lua.SpellRegistry;
import net.karneim.luamod.lua.Startup;
import net.karneim.luamod.lua.event.ModEventHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.sandius.rembulan.load.LoaderException;

@Mod(modid = LuaMod.MODID, version = LuaMod.VERSION, acceptableRemoteVersions = "*")
public class LuaMod {

  public static final String MODID = "luamod";
  public static final String VERSION = "1.10.2-1.0.0-SNAPSHOT";

  @Instance(MODID)
  public static LuaMod instance;

  public final Logger logger = LogManager.getLogger(LuaMod.class.getName());

  private MinecraftServer server;

  private final SpellRegistry spellRegistry = new SpellRegistry();
  private final ClipboardRegistry clipboards = new ClipboardRegistry();
  private final ModEventHandler modEventHandler = new ModEventHandler(this);
  private final SpellEntityFactory spellEntityFactory = new SpellEntityFactory(this);

  private ModConfiguration configuration;
  private File luaDir;
  private Profiles profiles;
  private FileCache fileCache;
  private LuaFunctionBinaryCache luaFunctionCache;
  private GistRepo gistRepo;
  private CredentialsStore credentialsStore;
  private Startup startup;

  private Long ticksLimit;
  private long DEFAULT_TICKS_LIMIT = 10000;

  private Permissions permissions;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    File configFile = event.getSuggestedConfigurationFile();
    File configDir = createConfigDirectory(configFile);
    luaDir = createLuaDirectory(configDir);
    fileCache = new FileCache(luaDir);
    luaFunctionCache = new LuaFunctionBinaryCache();
    configuration = new ModConfiguration(configFile);
    profiles = new Profiles(configuration);
    credentialsStore = new CredentialsStore(configuration);
    gistRepo = new GistRepo(fileCache);
    startup = new Startup(this, configuration);
    permissions = new Permissions(configuration);
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    EntityRegistry.registerModEntity(SpellEntity.class, "Spell", 1, this, 0, 1, false);
    MinecraftForge.EVENT_BUS.register(modEventHandler);
  }

  @EventHandler
  public void serverLoad(FMLServerStartingEvent event) {
    logger.info("Registering LuaMod Commands");
    event.registerServerCommand(new CommandLua());
    event.registerServerCommand(new CommandAdmin());
    event.registerServerCommand(new CommandMessagePatched(modEventHandler));
    server = checkNotNull(event.getServer());

    ForgeChunkManager.setForcedChunkLoadingCallback(instance,
        new net.minecraftforge.common.ForgeChunkManager.LoadingCallback() {
          @Override
          public void ticketsLoaded(List<Ticket> tickets, World world) {
            // This is called when the server is restarted and if there are tickets that we
            // have registered before.
            // Since we do not support to restore interrupted Lua programs, we do not need
            // to do here anything.
          }
        });
  }

  @EventHandler
  public void serverStarted(FMLServerStartedEvent event) {
    try {
      startup.runStartupProfile();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (LoaderException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @EventHandler
  public void serverStopping(FMLServerStoppingEvent event) {
    getSpellRegistry().breakAll();
  }

  public MinecraftServer getServer() {
    return server;
  }

  public SpellEntityFactory getSpellEntityFactory() {
    return spellEntityFactory;
  }

  public ModConfiguration getConfiguration() {
    return configuration;
  }

  public File getLuaDir() {
    return luaDir;
  }

  public long getTicksLimit() {
    if (ticksLimit == null) {
      String v = configuration.getStringOrDefault("runtime", "tickslimit",
          String.valueOf(DEFAULT_TICKS_LIMIT));
      ticksLimit = Long.parseLong(v);
    }
    return ticksLimit;
  }

  public void setTicksLimit(long aTicksLimit) {
    this.ticksLimit = aTicksLimit;
    configuration.setString("runtime", "tickslimit", String.valueOf(aTicksLimit));
    configuration.save();
  }

  public Profiles getProfiles() {
    return profiles;
  }

  public SpellRegistry getSpellRegistry() {
    return spellRegistry;
  }

  private File createLuaDirectory(File configDir) {
    File luaDir = new File(configDir, "lua");
    luaDir.mkdirs();
    return luaDir;
  }

  private File createConfigDirectory(File configFile) {
    String configFileName = configFile.getName();
    String configDirName = configFileName.substring(0, configFileName.lastIndexOf('.'));
    File configDir = new File(configFile.getParentFile(), configDirName);
    configDir.mkdirs();
    return configDir;
  }

  public FileCache getLuaCache() {
    return fileCache;
  }

  public LuaFunctionBinaryCache getLuaFunctionCache() {
    return luaFunctionCache;
  }

  public CredentialsStore getCredentialsStore() {
    return credentialsStore;
  }

  public GistRepo getGistRepo() {
    return gistRepo;
  }

  public ClipboardRegistry getClipboards() {
    return clipboards;
  }

  public Startup getStartup() {
    return startup;
  }

  public Permissions getPermissions() {
    return permissions;
  }

}
