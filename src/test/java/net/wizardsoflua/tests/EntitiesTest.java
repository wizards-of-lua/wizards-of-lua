package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

public class EntitiesTest extends WolTestBase {

  // /test net.wizardsoflua.tests.EntitiesTest test_find_pigs
  @Test
  public void test_find_pigs() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s", pos.getX(), pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua pigs=Entities.find('@e[type=Pig]'); print(#pigs, pigs[1].name)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("1   Pig");
  }

  // /test net.wizardsoflua.tests.EntitiesTest test_summon_pig
  @Test
  public void test_summon_pig() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua pig=Entities.summon('pig'); print(pig.name)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("Pig");
  }

  // /test net.wizardsoflua.tests.EntitiesTest test_summon_pig_with_nbt
  @Test
  public void test_summon_pig_with_nbt() throws Exception {
    // Given:
    String expected = "some-test-pig";

    // When:
    mc().executeCommand("/lua pig=Entities.summon('pig',{CustomName='%s'}); print(pig.name)",
        expected);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

}
