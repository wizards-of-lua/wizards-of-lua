package net.wizardsoflua.tests;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class MobTest extends WolTestBase {

  // /test net.wizardsoflua.tests.MobTest test_pig_instanceOf_Creature
  @Test
  public void test_pig_instanceOf_Creature() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[name=testpig]')[1]; print(instanceOf(Mob,p))");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.MobTest test_ai_is_true
  @Test
  public void test_ai_is_true() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[name=testpig]')[1]; print(p.ai)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.MobTest test_ai_is_false
  @Test
  public void test_ai_is_false() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[name=testpig]')[1]; print(p.ai)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("false");
  }

  // /test net.wizardsoflua.tests.MobTest test_ai_is_writable
  @Test
  public void test_ai_is_writable() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon minecraft:pig %s %s %s {CustomName:testpig,NoAI:1}", pos.getX(),
        pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@e[name=testpig]')[1]; p.ai=true; print(p.ai)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
    List<Entity> actEntities = mc().findEntities("@e[name=testpig]");
    assertThat(actEntities).hasSize(1);
    assertThat(((EntityLiving) actEntities.get(0)).isAIDisabled()).as("isAIDisabled()").isFalse();
  }

}
