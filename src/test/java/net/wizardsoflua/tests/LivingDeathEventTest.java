package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;

public class LivingDeathEventTest extends WolTestBase {

  // /test net.wizardsoflua.tests.LivingDeathEventTest test_cause_is_outOfWorld
  @Test
  public void test_cause_is_outOfWorld() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();
    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua q=Events.collect('LivingDeathEvent'); e=q:next(); print(e.cause)");
    mc().executeCommand("/kill @e[type=pig,name=testpig]");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("Killed testpig");
    assertThat(mc().nextServerMessage()).isEqualTo("outOfWorld");
  }

  // /test net.wizardsoflua.tests.LivingDeathEventTest test_entity_is_testpig
  @Test
  public void test_entity_is_testpig() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();
    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua q=Events.collect('LivingDeathEvent'); e=q:next(); print(e.entity.name)");
    mc().executeCommand("/kill @e[type=pig,name=testpig]");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("Killed testpig");
    assertThat(mc().nextServerMessage()).isEqualTo("testpig");
  }

  // /test net.wizardsoflua.tests.LivingDeathEventTest test_entity_is_player
  @Test
  public void test_entity_is_player() {
    // Given:
    String playerName = mc().player().getName();

    // When:
    try {
      mc().executeCommand(
          "/lua q=Events.collect('LivingDeathEvent'); e=q:next(); print(e.entity.name)");
      mc().executeCommand("/kill @a[name=%s]", playerName);

      // Then:
      assertThat(mc().nextServerMessage()).startsWith(playerName + " fell out of the world");
      assertThat(mc().nextServerMessage()).startsWith("Killed " + playerName);
      assertThat(mc().nextServerMessage()).isEqualTo(playerName);

    } finally {
      mc().player().respawn();
    }
  }

}
