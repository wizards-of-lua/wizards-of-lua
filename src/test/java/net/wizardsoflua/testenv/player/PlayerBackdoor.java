package net.wizardsoflua.testenv.player;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wizardsoflua.spell.SpellUtil;
import net.wizardsoflua.testenv.WolTestEnvironment;
import net.wizardsoflua.testenv.net.ClientAction;

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

  public void perform(ClientAction action) {
    testEnv.runAndWait(() -> testEnv.getPacketDispatcher().sendTo(action, getDelegate()));
  }

  public void setPosition(BlockPos pos) {
    testEnv
        .runAndWait(() -> getDelegate().setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ()));
  }

  public BlockPos getBlockPos() {
    return getDelegate().getPosition();
  }

  public Vec3d getPositionLookingAt() {
    Vec3d result = SpellUtil.getPositionLookingAt(getDelegate());
    return result;
  }
  
  public BlockPos getBlockPosLookingAt() {
    return new BlockPos(SpellUtil.getPositionLookingAt(getDelegate()));
  }

}
