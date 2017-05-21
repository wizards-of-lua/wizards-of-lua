package net.wizardsoflua;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;

@RunWith(MinecraftJUnitRunner.class)
public class TestEnvironmentTest extends WolTestBase {

  @Test
  public void test_can_receive_ServerChatEvent() {
    // Given:
    String message = "hello";

    // When:
    server().post(newServerChatEvent(player(), message));

    // Then:
    Iterable<ServerChatEvent> act = server().chatEvents();
    assertThat(messagesOf(act)).containsExactly(message);
  }

  @Test
  public void test_can_receive_RightClickBlock_Event() {
    // Given:
    BlockPos pos = BlockPos.ORIGIN;
    
    // When:
    server().post(newRightClickBlockEvent(player(), pos));

    // Then:
    Iterable<RightClickBlock> act = server().rightClickBlockEvents();
    assertThat(positionsOf(act)).containsExactly(pos);
  }

}
