package net.wizardsoflua;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.annotations.VisibleForTesting;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.wizardsoflua.extension.InjectionScope;
import net.wizardsoflua.imc.TypedImc;
import net.wizardsoflua.imc.WizardsOfLuaConsumer;

@Mod(WizardsOfLua.MODID)
public class WizardsOfLua {
  public static final String MODID = "wol";
  public static final String NAME = "Wizards of Lua";
  public static final String CONFIG_NAME = "wizards-of-lua";
  public static final String VERSION = loadVersion();

  private static String loadVersion() {
    try {
      Properties props = new Properties();
      props.load(WizardsOfLua.class.getResource("/wol.properties").openStream());
      String version = props.getProperty("version", "<devel>");
      return version;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static final String URL = "http://www.wizards-of-lua.net";
  public static final Logger LOGGER = LogManager.getLogger();

  private final Path tempDir;

  // TODO Adrodoc 28.04.2019: Check for thread safety of creating root scope in mod loading thread
  // and server scopes in server threads
  private final InjectionScope rootScope;
  private final ServerScopeManager serverScopeManager;

  public WizardsOfLua() throws IOException {
    LOGGER.info("Initializing Wizards-of-Lua, Version " + VERSION);
    tempDir = Files.createTempDirectory("wizards-of-lua");

    rootScope = createRootScope();
    serverScopeManager = rootScope.getInstance(ServerScopeManager.class);
    rootScope.getInstance(AboutMessage.class); // Initialize AboutMessage

    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processImcMessages);
  }

  private InjectionScope createRootScope() {
    InjectionScope rootScope = new InjectionScope();
    rootScope.registerResource(WizardsOfLua.class, this);
    return rootScope;
  }

  public void processImcMessages(InterModProcessEvent event) {
    TypedImc.getMessages(event, WizardsOfLuaConsumer.class).forEach(it -> {
      it.accept(WizardsOfLua.this);
    });
  }

  public Path getTempDir() {
    return tempDir;
  }

  @VisibleForTesting
  public InjectionScope provideServerScope(MinecraftServer server) {
    return serverScopeManager.provideServerScope(server);
  }
}
