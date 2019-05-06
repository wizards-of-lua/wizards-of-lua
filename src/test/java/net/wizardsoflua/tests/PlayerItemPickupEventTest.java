package net.wizardsoflua.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;

public class PlayerItemPickupEventTest extends WolTestBase {
  BlockPos itemPos = mc().getWorldSpawnPoint();
  BlockPos playerPos = itemPos.east(2);

  @BeforeEach
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
    mc().executeCommand("/summon item %s %s %s {Item:{id:anvil,Count:1}}", itemPos.getX(),
        itemPos.getY(), itemPos.getZ());
    mc().clearEvents();
    mc().executeCommand(
        "/lua q=Events.collect('PlayerItemPickupEvent'); e=q:next(); print(e.player.name)");

    // When:
    mc().player().setPosition(itemPos);

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo(expected);
  }

}
