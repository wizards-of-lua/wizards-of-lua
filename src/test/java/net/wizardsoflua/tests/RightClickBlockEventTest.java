package net.wizardsoflua.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;

public class RightClickBlockEventTest extends WolTestBase {

  BlockPos playerPos = new BlockPos(0, 4, 0);
  BlockPos clickPos = new BlockPos(2, 5, 0);
  BlockPos blockPos = new BlockPos(1, 5, 0);

  @AfterEach
  @BeforeEach
  public void clearBlocks() {
    mc().setBlock(playerPos, Blocks.AIR);
    mc().setBlock(clickPos, Blocks.AIR);
    mc().setBlock(blockPos, Blocks.AIR);
  }

  // /test net.wizardsoflua.tests.RightClickBlockEventTest test
  @Test
  public void test() {
    // Given:
    BlockPos playerPos = new BlockPos(0, 4, -1);
    mc().player().setPosition(playerPos);
    mc().player().setRotationYaw(0);
    mc().player().setMainHandItem(new ItemStack(Blocks.SAND));
    BlockPos clickPos = new BlockPos(0, 3, 0);
    mc().setBlock(clickPos, Blocks.OBSIDIAN);
    EnumFacing facing = EnumFacing.WEST;
    String expected = format(clickPos);

    mc().executeCommand("/lua q=Events.collect('RightClickBlockEvent'); e=q:next(); print(e.pos)");

    // When:
    mc().player().rightclick(clickPos, facing);

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo(expected);
  }

}
