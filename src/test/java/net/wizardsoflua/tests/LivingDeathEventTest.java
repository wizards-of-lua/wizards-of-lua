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
    String customName = "testpig";
    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:"
        + quoteJson(toJsonString(customName)) + ",NoAI:1}", pos.getX(), pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua " //
        + "q=Events.collect('LivingDeathEvent');\n" //
        + "e=q:next();\n" //
        + "print(e.cause);\n" //
    );
    mc().executeCommand("/kill @e[type=pig,name=" + customName + "]");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("Killed " + customName);
    assertThat(mc().nextServerMessage()).isEqualTo("outOfWorld");
  }

  // /test net.wizardsoflua.tests.LivingDeathEventTest test_entity_is_testpig
  @Test
  public void test_entity_is_testpig() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();
    String customName = "testpig";
    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:"
        + quoteJson(toJsonString(customName)) + ",NoAI:1}", pos.getX(), pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua " //
        + "q=Events.collect('LivingDeathEvent');\n" //
        + "e=q:next();\n" //
        + "print(e.entity.name);\n" //
    );
    mc().executeCommand("/kill @e[type=pig,name=" + customName + "]");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("Killed " + customName);
    assertThat(mc().nextServerMessage()).isEqualTo(customName);
  }

  // /test net.wizardsoflua.tests.LivingDeathEventTest test_entity_is_player
  @Test
  public void test_entity_is_player() {
    // Given:
    String playerName = mc().player().getName();

    // When:
    try {
      mc().executeCommand("/lua " //
          + "q=Events.collect('LivingDeathEvent');\n" //
          + "e=q:next();\n" //
          + "print(e.entity.name);\n" //
      );
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
