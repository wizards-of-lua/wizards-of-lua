package net.wizardsoflua;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;

@RunWith(MinecraftJUnitRunner.class)
public class TestEnvironmentTest extends WolTestBase {

  // /test net.wizardsoflua.TestEnvironmentTest test_can_receive_ServerChatEvent
  @Test
  public void test_can_receive_ServerChatEvent() {
    // Given:
    String message = "hello";

    // When:
    mc().post(newServerChatEvent(mc().player(), message));

    // Then:
    Iterable<ServerChatEvent> act = mc().chatEvents();
    assertThat(messagesOf(act)).containsExactly(message);
  }

  // /test net.wizardsoflua.TestEnvironmentTest test_can_receive_RightClickBlock_Event
  @Test
  public void test_can_receive_RightClickBlock_Event() {
    // Given:
    BlockPos pos = BlockPos.ORIGIN;

    // When:
    mc().post(newRightClickBlockEvent(mc().player(), pos));

    // Then:
    Iterable<RightClickBlock> act = mc().rightClickBlockEvents();
    assertThat(positionsOf(act)).containsExactly(pos);
  }

  // /test net.wizardsoflua.TestEnvironmentTest test_can_receive_console_output
  @Test
  public void test_can_receive_console_output() {
    // Given:
    String message = "hello";

    // When:
    mc().player().sendMessage(new TextComponentString(message));

    // Then:
    Iterable<String> act = mc().getChatOutputOf(mc().player());
    assertThat(act).containsOnly(message);
  }

  // /test net.wizardsoflua.TestEnvironmentTest test_can_move_player_around
  @Test
  public void test_can_move_player_around() {
    // Given:
    BlockPos pos = new BlockPos(1, 4, 1);

    // When:
    mc().player().setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());

    // Then
    BlockPos act = mc().player().getPosition();
    assertThat(act).isEqualTo(pos);
  }

}
