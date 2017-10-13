package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class EventsTest extends WolTestBase {

  // /test net.wizardsoflua.tests.EventsTest test_register_LeftClickBlockEvent
  @Test
  public void test_register_LeftClickBlockEvent() {
    // Given:
    BlockPos playerPos = new BlockPos(0, 4, 0);
    mc().player().setPosition(playerPos);
    BlockPos clickPos = new BlockPos(0, 3, 0);
    mc().setBlock(clickPos, Blocks.DIRT);
    EnumFacing facing = EnumFacing.UP;
    String expected = format(clickPos);

    mc().executeCommand("/lua q=Events.register('LeftClickBlockEvent'); e=q:pop(); print(e.pos)");

    // When:
    mc().player().leftclick(clickPos, facing);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EventsTest test_register_RightClickBlockEvent
  @Test
  public void test_register_RightClickBlockEvent() {
    // Given:
    BlockPos playerPos = new BlockPos(0, 4, 0);
    mc().player().setPosition(playerPos);
    BlockPos clickPos = new BlockPos(0, 3, 0);
    mc().setBlock(clickPos, Blocks.DIRT);
    EnumFacing facing = EnumFacing.UP;
    Vec3d hitvec = new Vec3d(clickPos);
    EnumHand hand = EnumHand.MAIN_HAND;
    String expected = format(clickPos);

    mc().executeCommand("/lua q=Events.register('RightClickBlockEvent'); e=q:pop(); print(e.pos)");

    // When:
    mc().player().rightclick(clickPos, facing, hitvec, hand);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

}
