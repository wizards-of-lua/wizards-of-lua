package net.wizardsoflua.tests;

import static net.minecraft.util.EnumFacing.UP;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.block.BlockSand;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;

@RunWith(MinecraftJUnitRunner.class)
public class BlockPlaceEventTest extends WolTestBase {

  BlockPos playerPos = new BlockPos(0, 4, 0);
  BlockPos blockPos = new BlockPos(1, 4, 0);
  BlockPos clickPos = new BlockPos(1, 3, 0);

  @After
  @Before
  public void clearBlocks() {
    mc().setBlock(playerPos, Blocks.AIR);
    mc().setBlock(blockPos, Blocks.AIR);
    mc().setBlock(clickPos, Blocks.DIRT);
  }

  // /test net.wizardsoflua.tests.BlockPlaceEventTest test__BlockPlaceEvent_MAIN_HAND
  @Test
  public void test__BlockPlaceEvent_MAIN_HAND() {
    // Given:
    BlockSand sand = Blocks.SAND;
    mc().player().setMainHandItem(new ItemStack(sand));
    mc().player().setPosition(playerPos);
    mc().executeCommand("lua q=Events.connect('BlockPlaceEvent')\n"//
        + "e=q:next()\n"//
        + "print(e.pos)\n"//
        + "print(e.block.name)\n"//
        + "print(e.hand)\n"//
        + "print(e.placedAgainst.name)\n"//
    );

    // When:
    mc().player().rightclick(clickPos, UP);

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo(format(blockPos));
    assertThat(mc().nextServerMessage()).isEqualTo(sand.getRegistryName().getResourcePath());
    assertThat(mc().nextServerMessage()).isEqualTo(EnumHand.MAIN_HAND.toString());
    assertThat(mc().nextServerMessage()).isEqualTo("dirt");
  }

  // /test net.wizardsoflua.tests.BlockPlaceEventTest test__BlockPlaceEvent_OFF_HAND
  @Test
  public void test__BlockPlaceEvent_OFF_HAND() {
    // Given:
    BlockSand sand = Blocks.SAND;
    mc().player().setOffHandItem(new ItemStack(sand));
    mc().player().setPosition(playerPos);
    mc().executeCommand("lua q=Events.connect('BlockPlaceEvent')\n"//
        + "e=q:next()\n"//
        + "print(e.pos)\n"//
        + "print(e.block.name)\n"//
        + "print(e.hand)\n"//
        + "print(e.placedAgainst.name)\n"//
    );

    // When:
    mc().player().rightclick(clickPos, UP);

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo(format(blockPos));
    assertThat(mc().nextServerMessage()).isEqualTo(sand.getRegistryName().getResourcePath());
    assertThat(mc().nextServerMessage()).isEqualTo(EnumHand.OFF_HAND.toString());
    assertThat(mc().nextServerMessage()).isEqualTo("dirt");
  }

  // /test net.wizardsoflua.tests.BlockPlaceEventTest test_cancel
  @Test
  public void test_cancel() {
    // Given:
    BlockSand sand = Blocks.SAND;
    mc().player().setMainHandItem(new ItemStack(sand));
    mc().player().setPosition(playerPos);

    // When:
    mc().executeCommand("lua Events.on('BlockPlaceEvent'):call(function(event)\n"//
        + "event.canceled = true\n"//
        + "end)\n"//
    );
    mc().player().rightclick(clickPos, UP);

    // Then:
    assertThat(mc().getBlock(blockPos).getBlock()).isEqualTo(Blocks.AIR);
  }

}
