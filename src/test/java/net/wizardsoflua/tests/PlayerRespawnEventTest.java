package net.wizardsoflua.tests;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class PlayerRespawnEventTest extends WolTestBase {
  BlockPos playerPos = new BlockPos(0, 4, 0);

  @After
  public void resetPlayerPosition() {
    mc().player().setPosition(playerPos);
  }

  // /test net.wizardsoflua.tests.PlayerRespawnEventTest test
  @Test
  public void test() {
    // Given:
    String expected1 = mc().player().getName() + " fell out of the world";
    String expected5 = "#" + mc().player().getName();
    mc().executeCommand(
        "/lua q=Events.connect('PlayerRespawnEvent'); e=q:pop(); print('#'..e.player.name)");
    mc().player().chat("/kill %s", mc().player().getName());

    // When:
    mc().player().respawn();

    // Then:
    ServerLog4jEvent act1 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act1.getMessage()).startsWith(expected1);
    ServerLog4jEvent act5 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act5.getMessage()).isEqualTo(expected5);
  }

}
