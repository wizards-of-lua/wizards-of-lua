package net.wizardsoflua.testenv.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TestPlayerPreparedForTestEvent extends Event {
  private final EntityPlayerMP player;
  private final int id;

  public TestPlayerPreparedForTestEvent(EntityPlayerMP player, int id) {
    this.player = player;
    this.id = id;
  }

  public EntityPlayerMP getPlayer() {
    return player;
  }

  public int getId() {
    return id;
  }

}
