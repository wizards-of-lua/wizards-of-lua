package net.wizardsoflua.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class SwingArmEventTest extends WolTestBase {
  BlockPos playerPos = new BlockPos(0, 4, 0);
  BlockPos clickPos = new BlockPos(2, 5, 0);
  BlockPos blockPos = new BlockPos(1, 5, 0);

  @After
  @Before
  public void clearBlocks() {
    mc().setBlock(playerPos, Blocks.AIR);
    mc().setBlock(clickPos, Blocks.AIR);
    mc().setBlock(blockPos, Blocks.AIR);
  }

  // /test net.wizardsoflua.tests.SwingArmEventTest test_rightclick
  @Test
  public void test_rightclick() {
    // Given:
    mc().player().setPosition(playerPos);
    ItemStack item = mc().getItemStack(Blocks.SAND);
    mc().player().setMainHandItem(item);
    mc().setBlock(clickPos, Blocks.OBSIDIAN);
    EnumHand hand = EnumHand.MAIN_HAND;
    EnumFacing facing = EnumFacing.WEST;
    Vec3d hitvec = new Vec3d(clickPos);
    String expected = hand.name();

    mc().executeCommand("/lua q=Events.connect('SwingArmEvent'); e=q:next(); print(e.hand)");

    // When:
    mc().player().rightclick(clickPos, facing, hitvec, hand);

    // Then:
    RightClickBlock act1 = mc().waitFor(RightClickBlock.class);
    assertThat(act1.getPos()).isEqualTo(clickPos);
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SwingArmEventTest test_leftclick
  @Test
  public void test_leftclick() {
    // Given:
    mc().player().setPosition(playerPos);
    BlockPos clickPos = new BlockPos(0, 3, 0);
    mc().setBlock(clickPos, Blocks.DIRT);
    EnumFacing facing = EnumFacing.UP;
    String expected = EnumHand.MAIN_HAND.name();

    mc().executeCommand("/lua q=Events.connect('SwingArmEvent'); e=q:next(); print(e.hand)");

    // When:
    mc().player().leftclick(clickPos, facing);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

}
