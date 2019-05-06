package net.wizardsoflua.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;

public class PlayerRespawnEventTest extends WolTestBase {
  BlockPos playerPos = new BlockPos(0, 4, 0);

  @AfterEach
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
        "/lua q=Events.collect('PlayerRespawnEvent'); e=q:next(); print('#'..e.player.name)");
    mc().player().chat("/kill %s", mc().player().getName());

    // When:
    mc().player().respawn();

    // Then:
    assertThat(mc().nextServerMessage()).startsWith(expected1);
    assertThat(mc().nextServerMessage()).isEqualTo(expected5);
  }

}
