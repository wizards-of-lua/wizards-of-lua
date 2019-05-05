package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;

public class EntitiesTest extends WolTestBase {

  // /test net.wizardsoflua.tests.EntitiesTest test_find_pigs
  @Test
  public void test_find_pigs() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();
    mc().executeCommand("/summon minecraft:pig %s %s %s", pos.getX(), pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua " //
        + "pigs=Entities.find('@e[type=pig]');\n" //
        + "print(#pigs, pigs[1].name);\n" //
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("1   Pig");
  }

  // /test net.wizardsoflua.tests.EntitiesTest test_summon_pig
  @Test
  public void test_summon_pig() throws Exception {
    // When:
    mc().executeCommand("/lua " //
        + "pig=Entities.summon('pig');\n" //
        + "print(pig.name);\n" //
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("Pig");
  }

  // /test net.wizardsoflua.tests.EntitiesTest test_summon_pig_with_nbt
  @Test
  public void test_summon_pig_with_nbt() throws Exception {
    // Given:
    String expected = "some-test-pig";

    // When:
    mc().executeCommand("/lua " //
        + "pig=Entities.summon('pig', {CustomName='" + toJsonString(expected) + "'});\n" //
        + "print(pig.name);\n" //
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo(expected);
  }
}
