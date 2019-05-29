package net.wizardsoflua.tests;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
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

  // /test net.wizardsoflua.tests.EntitiesTest test_find_pig_by_uuid
  @Test
  public void test_find_pig_by_uuid() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();
    String tag = "testpig";

    mc().executeCommand("/summon minecraft:pig %s %s %s {Tags:[" + tag + "]}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();
    List<Entity> pigs = mc().findEntities("@e[tag=" + tag + "]");

    // Expect:
    assertThat(pigs).hasSize(1);
    Entity pig = pigs.get(0);

    // When:
    mc().executeCommand(
        "/lua pigs=Entities.find('" + pig.getUniqueID() + "'); print(#pigs, pigs[1].name)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("1   Pig");
  }

  // /test net.wizardsoflua.tests.EntitiesTest test_find_player_by_name
  @Test
  public void test_find_player_by_name() throws Exception {
    // Given:
    EntityPlayerMP player = mc().getTestEnv().getTestPlayer();
    String name = player.getName();

    // When:
    mc().executeCommand(
        "/lua players=Entities.find('" + name + "'); print(#players, players[1].name)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("1   " + name);
  }

  // /test net.wizardsoflua.tests.EntitiesTest test_summon_pig
  @Test
  public void test_summon_pig() throws Exception {
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
