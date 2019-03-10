package net.wizardsoflua.testenv.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.eventbus.api.Event;

public class TestPlayerReceivedChatEvent extends Event {
  private final EntityPlayerMP player;
  private final String message;

  public TestPlayerReceivedChatEvent(EntityPlayerMP player, String message) {
    this.player = player;
    this.message = message;
  }

  public EntityPlayerMP getPlayer() {
    return player;
  }

  public String getMessage() {
    return message;
  }

}
