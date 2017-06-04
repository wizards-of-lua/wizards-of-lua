package net.wizardsoflua;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
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
    mc().post(newServerChatEvent(mc().player(), message));

    // Then:
    Iterable<ServerChatEvent> act = mc().chatEvents();
    assertThat(messagesOf(act)).containsExactly(message);
  }

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
  
  @Test
  public void test_can_receive_colsole_output() {
    // Given:
    String message = "hello";

    // When:
    mc().player().sendMessage(new TextComponentString(message));

    // Then:
    Iterable<String> act = mc().getChatOutputOf(mc().player());
    assertThat(act).containsOnly(message);
  }

}
