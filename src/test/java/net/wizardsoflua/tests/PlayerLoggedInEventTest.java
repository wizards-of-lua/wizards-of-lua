package net.wizardsoflua.tests;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class PlayerLoggedInEventTest extends WolTestBase {
  BlockPos playerPos = new BlockPos(0, 4, 0);

  @After
  public void resetPlayerPosition() {
    mc().player().waitForPlayer(30000);
    mc().player().setPosition(playerPos);
  }

  // /test net.wizardsoflua.tests.PlayerLoggedInEventTest test
  @Test
  public void test() {
    // Given:
    String expected1 = mc().player().getName() + " lost connection";
    String expected2 = mc().player().getName() + " left the game";
    String expected3 = mc().player().getName() + ".+ logged in .+";
    String expected4 = mc().player().getName() + " joined the game";
    String expected5 = "#" + mc().player().getName();
    mc().executeCommand(
        "/lua q=Events.connect('PlayerLoggedInEvent'); e=q:pop(); print('#'..e.player.name)");

    // When:
    mc().player().reconnect();

    // Then:
    ServerLog4jEvent act1 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act1.getMessage()).startsWith(expected1);
    ServerLog4jEvent act2 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act2.getMessage()).isEqualTo(expected2);
    ServerLog4jEvent act3 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act3.getMessage()).matches(expected3);
    ServerLog4jEvent act4 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act4.getMessage()).isEqualTo(expected4);
    ServerLog4jEvent act5 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act5.getMessage()).isEqualTo(expected5);
  }

}
