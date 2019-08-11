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
    String expected = "#" + mc().player().getName();
    mc().executeCommand("/kill %s", mc().player().getName());
    mc().clearEvents();
    mc().executeCommand(
        "/lua q=Events.collect('PlayerRespawnEvent'); e=q:next(); print('#'..e.player.name)");

    // When:
    mc().player().respawn();

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo(expected);
  }

}
