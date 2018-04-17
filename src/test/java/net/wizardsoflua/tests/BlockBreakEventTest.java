package net.wizardsoflua.tests;

import static net.minecraft.util.EnumFacing.UP;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class BlockBreakEventTest extends WolTestBase {

  BlockPos playerPos = new BlockPos(0, 4, 0);
  BlockPos blockPos = new BlockPos(1, 4, 0);

  @After
  @Before
  public void clearBlocks() {
    mc().setBlock(playerPos, Blocks.AIR);
    mc().setBlock(blockPos, Blocks.AIR);
  }

  @After
  public void after() {
    mc().executeCommand("kill @e[type=item]");
  }

  // /test net.wizardsoflua.tests.BlockBreakEventTest test__BlockBreakEvent
  @Test
  public void test__BlockBreakEvent() {
    // Given:
    mc().player().setPosition(playerPos);
    mc().setBlock(blockPos, Blocks.TNT); // TNT is destroyed by a single hit
    String expected = format(blockPos);

    mc().executeCommand("/lua q=Events.collect('BlockBreakEvent'); e=q:next(); print(e.pos)");

    // When:
    mc().player().leftclick(blockPos, UP);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

}
