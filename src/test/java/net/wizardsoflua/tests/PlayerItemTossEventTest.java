package net.wizardsoflua.tests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class PlayerItemTossEventTest extends WolTestBase {
  BlockPos itemPos = mc().getWorldSpawnPoint();
  BlockPos playerPos = itemPos.east(2);

  @Before
  public void before() {
    mc().player().setPosition(playerPos);
    sleep(100);
  }

  // /test net.wizardsoflua.tests.PlayerItemTossEventTest test
  @Test
  public void test() {
    // Given:
    mc().player().setPosition(playerPos);
    String expected = "iron_axe";
    mc().executeCommand("/give %s %s", mc().player().getName(), expected);
    mc().clearEvents();
    mc().executeCommand(
        "/lua q=Events.collect('PlayerItemTossEvent'); e=q:next(); print(e.item.item.id)");

    // When:
    mc().player().tossItemFromInventory(0);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

}
