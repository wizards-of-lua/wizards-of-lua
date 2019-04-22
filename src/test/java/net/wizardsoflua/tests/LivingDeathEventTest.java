package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

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
    ServerLog4jEvent act1 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act1.getMessage()).isEqualTo("Killed testpig");
    ServerLog4jEvent act2 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act2.getMessage()).isEqualTo("outOfWorld");
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
    ServerLog4jEvent act1 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act1.getMessage()).isEqualTo("Killed testpig");
    ServerLog4jEvent act2 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act2.getMessage()).isEqualTo("testpig");
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
      ServerLog4jEvent act1 = mc().waitFor(ServerLog4jEvent.class);
      assertThat(act1.getMessage()).startsWith(playerName + " fell out of the world");
      ServerLog4jEvent act2 = mc().waitFor(ServerLog4jEvent.class);
      assertThat(act2.getMessage()).startsWith("Killed " + playerName);
      ServerLog4jEvent act3 = mc().waitFor(ServerLog4jEvent.class);
      assertThat(act3.getMessage()).isEqualTo(playerName);

    } finally {
      mc().player().respawn();
    }
  }

}
