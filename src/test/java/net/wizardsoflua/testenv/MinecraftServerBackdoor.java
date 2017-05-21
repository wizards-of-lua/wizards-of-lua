package net.wizardsoflua.testenv;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class MinecraftServerBackdoor {

  private final MinecraftServer server;
  private final WolTestEnvironment testEnv;
  private final EventBus eventBus;

  public MinecraftServerBackdoor(MinecraftServer server, EventBus eventBus,
      WolTestEnvironment testEnv) {
    this.server = server;
    this.eventBus = eventBus;
    this.testEnv = testEnv;
  }

  public void post(ServerChatEvent event) {
    eventBus.post(event);
  }

  public Iterable<ServerChatEvent> chatEvents() {
    return testEnv.getEvents(ServerChatEvent.class);
  }

  public String getName() {
    return server.getName();
  }

}
