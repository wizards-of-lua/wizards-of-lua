package net.wizardsoflua.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class LeftClickBlockEventsTest extends WolTestBase {

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

  // /test net.wizardsoflua.tests.LeftClickBlockEventsTest test
  @Test
  public void test() {
    // Given:
    BlockPos playerPos = new BlockPos(0, 4, -1);
    mc().player().setPosition(playerPos);
    mc().player().setRotationYaw(0);
    BlockPos clickPos = new BlockPos(0, 3, 0);
    mc().setBlock(clickPos, Blocks.DIRT);
    EnumFacing facing = EnumFacing.UP;
    String expected = format(clickPos);

    mc().executeCommand("/lua q=Events.connect('LeftClickBlockEvent'); e=q:pop(); print(e.pos)");

    // When:
    mc().player().leftclick(clickPos, facing);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

}
