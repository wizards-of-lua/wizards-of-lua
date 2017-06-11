package net.wizardsoflua.testenv.player;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestEnvironment;
import net.wizardsoflua.testenv.client.ClientAction;
import net.wizardsoflua.testenv.net.PacketDispatcher;

public class PlayerBackdoor {
  private WolTestEnvironment testEnv;

  public PlayerBackdoor(WolTestEnvironment testEnv) {
    this.testEnv = testEnv;
  }

  public EntityPlayerMP getDelegate() {
    EntityPlayerMP testPlayer = testEnv.getTestPlayer();
    checkNotNull(testPlayer, "testPlayer==null!");
    return testPlayer;
  }

  public void perform(ClientAction<?> action) {
    testEnv.getServer().addScheduledTask(new Runnable() {
      @Override
      public void run() {
        testEnv.getPacketDispatcher().sendTo(action, getDelegate());
      }
    });
  }

  public void setPosition(BlockPos pos) {
    getDelegate().setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
  }

}
