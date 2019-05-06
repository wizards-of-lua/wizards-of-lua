package net.wizardsoflua.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.wizardsoflua.testenv.WolTestBase;

public class PlayerChangedDimensionEventTest extends WolTestBase {

  BlockPos portalPos = new BlockPos(-29, 3, 6);
  BlockPos playerPos = portalPos.up().north().east(2);

  @BeforeEach
  public void before() {
    sleep(1000);
    if (mc().player().getTestPlayer().dimension != DimensionType.OVERWORLD) {
      mc().player().changeDimension(DimensionType.OVERWORLD);
    }
    mc().player().setPosition(playerPos);
    sleep(1000);
    mc().clearEvents();
  }

  @AfterEach
  public void after() {
    sleep(1000);
    mc().player().changeDimension(DimensionType.OVERWORLD);
    mc().player().setPosition(playerPos);
    sleep(1000);
    deletePortal(portalPos);
  }

  // /test net.wizardsoflua.tests.PlayerChangedDimensionEventTest test
  @Test
  public void test() {
    // Given:
    String expected = mc().player().getName();
    String sometimesExpected = mc().player().getName() + " moved too quickly!";
    createPortal(portalPos);
    mc().executeCommand(
        "/lua q=Events.collect('PlayerChangedDimensionEvent'); e=q:next(); print(e.player.name)");

    // When:
    mc().player().setPosition(portalPos.up().north());

    // Then:
    String actual = mc().nextServerMessage();
    if (actual.startsWith(sometimesExpected)) {
      actual = mc().nextServerMessage();
    }
    assertThat(actual).isEqualTo(expected);
  }

  private void createPortal(BlockPos startPos) {
    frame(startPos, Blocks.OBSIDIAN);
    mc().setBlock(startPos.up().north(), Blocks.FIRE);
  }

  private void deletePortal(BlockPos startPos) {
    frame(startPos, Blocks.AIR);
  }

  private void frame(BlockPos startPos, Block blockType) {
    mc().bar(startPos, EnumFacing.NORTH, 4, blockType);
    mc().bar(startPos, EnumFacing.UP, 4, blockType);
    mc().bar(startPos.up(4), EnumFacing.NORTH, 4, blockType);
    mc().bar(startPos.north(3), EnumFacing.UP, 4, blockType);
    mc().bar(startPos.up().north(), EnumFacing.UP, 3, Blocks.AIR);
    mc().bar(startPos.up().north(2), EnumFacing.UP, 3, Blocks.AIR);
  }

}
