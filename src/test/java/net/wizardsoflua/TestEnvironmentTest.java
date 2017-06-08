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
    mc().post(newServerChatEvent(mc().player().getDelegate(), message));

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
    mc().post(newRightClickBlockEvent(mc().player().getDelegate(), pos));

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
    mc().player().getDelegate().sendMessage(new TextComponentString(message));

    // Then:
    Iterable<String> act = mc().getChatOutputOf(mc().player().getDelegate());
    assertThat(act).containsOnly(message);
  }

  // /test net.wizardsoflua.TestEnvironmentTest test_can_move_player_around
  @Test
  public void test_can_move_player_around() {
    // Given:
    BlockPos pos = new BlockPos(1, 4, 1);

    // When:
    mc().player().getDelegate().setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());

    // Then
    BlockPos act = mc().player().getDelegate().getPosition();
    assertThat(act).isEqualTo(pos);
  }
  
  // /test net.wizardsoflua.TestEnvironmentTest test_player_can_post_chat_Message
  @Test
  public void test_player_can_post_chat_Message() {
    // Given:
    String message = "hello";

    // When:
    mc().player().sendChatMessage(message);

    // Then:
    // FIXME wait for events
    ServerChatEvent act = mc().waitForChatEvent();
    assertThat(act.getMessage()).isEqualTo(message);
  }

}
