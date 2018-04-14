package net.wizardsoflua.tests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class PlayerItemPickupEventTest extends WolTestBase {
  BlockPos itemPos = mc().getWorldSpawnPoint();
  BlockPos playerPos = itemPos.east(2);

  @Before
  public void before() {
    mc().player().setPosition(playerPos);
    sleep(100);
  }

  // /test net.wizardsoflua.tests.PlayerItemPickupEventTest test
  @Test
  public void test() {
    // Given:
    mc().player().setPosition(playerPos);
    String expected = mc().player().getName();
    mc().executeCommand("/summon Item %s %s %s {Item:{id:anvil,Count:1},CustomName:testitem}",
        itemPos.getX(), itemPos.getY(), itemPos.getZ());
    mc().clearEvents();
    mc().executeCommand(
        "/lua q=Events.collect('PlayerItemPickupEvent'); e=q:next(); print(e.player.name)");

    // When:
    mc().player().setPosition(itemPos);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

}
