package net.wizardsoflua.tests;

import java.util.List;
import org.junit.jupiter.api.Test;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;

public class MobTest extends WolTestBase {

  // /test net.wizardsoflua.tests.MobTest test_pig_instanceOf_Mob
  @Test
  public void test_pig_instanceOf_Mob() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {Tags:[testpig]}", pos.getX(), pos.getY(),
        pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[tag=testpig]')[1]; print(instanceOf(Mob,p))");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.MobTest test_ai_is_true
  @Test
  public void test_ai_is_true() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {Tags:[testpig]}", pos.getX(), pos.getY(),
        pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[tag=testpig]')[1]; print(p.ai)");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.MobTest test_ai_is_false
  @Test
  public void test_ai_is_false() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {Tags:[testpig],NoAI:1}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[tag=testpig]')[1]; print(p.ai)");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("false");
  }

  // /test net.wizardsoflua.tests.MobTest test_ai_is_writable
  @Test
  public void test_ai_is_writable() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {Tags:[testpig],NoAI:1}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[tag=testpig]')[1]; p.ai=true; print(p.ai)");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("true");
    List<? extends Entity> actEntities = mc().findEntities("@e[tag=testpig]");
    assertThat(actEntities).hasSize(1);
    assertThat(((EntityLiving) actEntities.get(0)).isAIDisabled()).as("isAIDisabled()").isFalse();
  }

  // /test net.wizardsoflua.tests.MobTest test_health_is_readable
  @Test
  public void test_health_is_readable() throws Exception {
    // Given
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {Tags:[testpig]}", pos.getX(), pos.getY(),
        pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[tag=testpig]')[1]; print(p.health)");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("10.0");
  }

  // /test net.wizardsoflua.tests.MobTest test_health_is_readable
  @Test
  public void test_health_is_writable() throws Exception {
    // Given
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {Tags:[testpig]}", pos.getX(), pos.getY(),
        pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[tag=testpig]')[1]; p.health=5.5; print(p.health)");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("5.5");
  }

}
