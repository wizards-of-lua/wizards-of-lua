package net.wizardsoflua;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.wizardsoflua.event.WolEventHandler;
import net.wizardsoflua.extension.ExtensionLoader;
import net.wizardsoflua.extension.InjectionScope;
import net.wizardsoflua.extension.api.inject.PostConstruct;
import net.wizardsoflua.extension.api.inject.PreDestroy;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.server.api.ServerScoped;
import net.wizardsoflua.extension.server.spi.CommandRegisterer;
import net.wizardsoflua.rest.WolRestApiServer;
import net.wizardsoflua.startup.Startup;

@Singleton
public class ServerScopeManager {
  @Resource
  private InjectionScope parentScope;

  @PostConstruct
  private void postConstruct() {
    MinecraftForge.EVENT_BUS.register(this);
  }

  @PreDestroy
  private void preDestroy() {
    MinecraftForge.EVENT_BUS.unregister(this);
  }

  private final Map<MinecraftServer, InjectionScope> serverScopes = new HashMap<>();

  @SubscribeEvent
  public void onServerStarting(FMLServerStartingEvent event) throws IOException {
    InjectionScope serverScope = provideServerScope(event.getServer());

    CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
    ExtensionLoader.getCommandRegisterers().forEach(cls -> {
      CommandRegisterer instance = serverScope.getInstance(cls);
      instance.register(dispatcher);
    });

    serverScope.getInstance(Startup.class); // Initialize Startup
    serverScope.getInstance(WolEventHandler.class); // Initialize main event handler
    serverScope.getInstance(WolRestApiServer.class); // Initialize rest server
  }

  public InjectionScope provideServerScope(MinecraftServer server) {
    return serverScopes.computeIfAbsent(server, this::createServerScope);
  }

  private InjectionScope createServerScope(MinecraftServer server) {
    InjectionScope result = parentScope.createSubScope(ServerScoped.class);
    result.registerResource(MinecraftServer.class, server);
    return result;
  }

  @SubscribeEvent
  public void onServerStopping(FMLServerStoppingEvent event) {
    MinecraftServer server = event.getServer();
    InjectionScope serverScope = serverScopes.remove(server);
    if (serverScope != null) {
      serverScope.close();
    }
  }
}
