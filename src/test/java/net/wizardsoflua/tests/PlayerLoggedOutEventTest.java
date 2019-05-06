package net.wizardsoflua.tests;

import static net.minecraftforge.api.distmarker.Dist.CLIENT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.junit.DisabledOnDist;

@DisabledOnDist(CLIENT)
public class PlayerLoggedOutEventTest extends WolTestBase {
  BlockPos playerPos = new BlockPos(0, 4, 0);

  @AfterEach
  public void resetPlayerPosition() {
    mc().player().setPosition(playerPos);
  }

  // /test net.wizardsoflua.tests.PlayerLoggedOutEventTest test
  @Test
  public void test() {
    // Given:
    String expected1 = mc().player().getName() + " lost connection";
    String expected2 = mc().player().getName() + " left the game";
    String expected3 = "#" + mc().player().getName();
    mc().executeCommand(
        "/lua q=Events.collect('PlayerLoggedOutEvent'); e=q:next(); print('#'..e.player.name)");

    // When:
    mc().player().reconnect();

    // Then:
    assertThat(mc().nextServerMessage()).startsWith(expected1);
    assertThat(mc().nextServerMessage()).isEqualTo(expected2);
    assertThat(mc().nextServerMessage()).isEqualTo(expected3);
  }

}
