package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class EventsTest extends WolTestBase {

  // /test net.wizardsoflua.tests.EventsTest test_connect_LeftClickBlockEvent
  @Test
  public void test_connect_LeftClickBlockEvent() {
    // Given:
    BlockPos playerPos = new BlockPos(0, 4, -1);
    mc().player().setPosition(playerPos);
    mc().player().setRotationYaw(0);
    BlockPos clickPos = new BlockPos(0, 3, 0);
    mc().setBlock(clickPos, Blocks.DIRT);
    EnumFacing facing = EnumFacing.UP;
    String expected = format(clickPos);

    mc().executeCommand("/lua q=Events.collect('LeftClickBlockEvent'); e=q:next(); print(e.pos)");

    // When:
    mc().player().leftclick(clickPos, facing);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EventsTest test_on_Event_call__Two_custom_Events
  @Test
  public void test_on_Event_call__Two_custom_Events() {
    // When:
    mc().executeCommand("lua Events.on('custom-event'):call(function(event)\n"//
        + "print(event.data)\n"//
        + "end)\n"//
    );
    mc().executeCommand("lua Events.fire('custom-event', 1)");
    mc().executeCommand("lua Events.fire('custom-event', 2)");

    // Then:
    ServerLog4jEvent act1 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act1.getMessage()).isEqualTo("1");
    ServerLog4jEvent act2 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act2.getMessage()).isEqualTo("2");
  }

}
