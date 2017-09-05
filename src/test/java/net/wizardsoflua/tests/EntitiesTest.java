package net.wizardsoflua.tests;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class EntitiesTest extends WolTestBase {

  @After
  public void killNonPlayerEntities() {
    mc().executeCommand("/kill @e[type=!Player]");
  }

  // /test net.wizardsoflua.tests.EntitiesTest test_find_pigs
  @Test
  public void test_find_pigs() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s", pos.getX(), pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua pigs=Entities.find('@e[type=Pig]'); print(#pigs)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("1");
  }

  // /test net.wizardsoflua.tests.EntitiesTest test_find_pigs_2
  @Test
  public void test_find_pigs_2() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s", pos.getX(), pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua pigs=Entities.find('@e[type=Pig]'); print(pigs[1].name, type(pigs[1]))");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("Pig Entity");
  }

}
