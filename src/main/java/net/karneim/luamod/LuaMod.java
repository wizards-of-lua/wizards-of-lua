package net.karneim.luamod;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.karneim.luamod.cache.FileCache;
import net.karneim.luamod.config.ModConfiguration;
import net.karneim.luamod.credentials.CredentialsStore;
import net.karneim.luamod.cursor.ClipboardRegistry;
import net.karneim.luamod.gist.GistRepo;
import net.karneim.luamod.lua.CommandAdmin;
import net.karneim.luamod.lua.CommandSpell;
import net.karneim.luamod.lua.CommandMessagePatched;
import net.karneim.luamod.lua.SpellEntity;
import net.karneim.luamod.lua.SpellRegistry;
import net.karneim.luamod.lua.event.EventWrapper;
import net.karneim.luamod.lua.event.ModEventHandler;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod(modid = LuaMod.MODID, version = LuaMod.VERSION, acceptableRemoteVersions = "*")
public class LuaMod {

  public static final String MODID = "luamod";
  public static final String VERSION = "1.10.2-1.0.0-SNAPSHOT";

  @Instance(MODID)
  public static LuaMod instance;

  public final Logger logger = LogManager.getLogger(LuaMod.class.getName());

  private final SpellRegistry processRegistry = new SpellRegistry();
  private final ClipboardRegistry clipboards = new ClipboardRegistry();

  private ModConfiguration configuration;
  private File luaDir;
  private ProfileUrls profileUrls;
  private FileCache fileCache;
  private GistRepo gistRepo;
  private CredentialsStore credentialsStore;

  private long defaultTicksLimit = 10000;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    File configFile = event.getSuggestedConfigurationFile();
    File configDir = createConfigDirectory(configFile);
    luaDir = createLuaDirectory(configDir);
    fileCache = new FileCache(luaDir);
    configuration = new ModConfiguration(configFile);
    profileUrls = new ProfileUrls(configuration);
    credentialsStore = new CredentialsStore(configuration);
    gistRepo = new GistRepo(fileCache);
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    EntityRegistry.registerModEntity(SpellEntity.class, "Spell", 1, this, 0, 1, false);
    MinecraftForge.EVENT_BUS.register(new ModEventHandler(this));
  }

  @EventHandler
  public void serverLoad(FMLServerStartingEvent event) {
    logger.info("Registering LuaMod Commands");
    event.registerServerCommand(new CommandSpell());
    event.registerServerCommand(new CommandAdmin());
    event.registerServerCommand(new CommandMessagePatched(this));

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

  public ModConfiguration getConfiguration() {
    return configuration;
  }

  public File getLuaDir() {
    return luaDir;
  }

  public long getDefaultTicksLimit() {
    return defaultTicksLimit;
  }

  public void setDefaultTicksLimit(long defaultTicksLimit) {
    this.defaultTicksLimit = defaultTicksLimit;
  }

  public ProfileUrls getProfileUrls() {
    return profileUrls;
  }

  public SpellRegistry getProcessRegistry() {
    return processRegistry;
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

  public CredentialsStore getCredentialsStore() {
    return credentialsStore;
  }

  public GistRepo getGistRepo() {
    return gistRepo;
  }

  public ClipboardRegistry getClipboards() {
    return clipboards;
  }

  public void notifyEventListeners(EventWrapper<?> wrapper) {
    for (SpellEntity entity : processRegistry.getAll()) {
      entity.notifyEventListeners(wrapper);
    }
  }

}
