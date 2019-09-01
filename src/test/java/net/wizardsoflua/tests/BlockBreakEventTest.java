package net.wizardsoflua.tests;

import static net.minecraft.util.EnumFacing.UP;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;

public class BlockBreakEventTest extends WolTestBase {

  BlockPos playerPos = new BlockPos(0, 4, 0);
  BlockPos blockPos = new BlockPos(1, 4, 0);

  @AfterEach
  @BeforeEach
  public void clearBlocks() {
    mc().setBlock(playerPos, Blocks.AIR);
    mc().setBlock(blockPos, Blocks.AIR);
  }

  @AfterEach
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
    mc().player().leftClick(blockPos, UP);

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo(expected);
  }

}
