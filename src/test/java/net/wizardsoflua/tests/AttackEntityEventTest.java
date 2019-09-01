package net.wizardsoflua.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

public class AttackEntityEventTest extends WolTestBase {
  BlockPos pigPos = mc().getWorldSpawnPoint();
  BlockPos playerPos = pigPos.east(2);

  @BeforeEach
  public void before() {
    mc().player().setPosition(playerPos);
    sleep(100);
  }

  // /test net.wizardsoflua.tests.AttackEntityEventTest test
  @Test
  public void test() {
    // Given:
    mc().player().setPosition(playerPos);
    mc().executeCommand("/summon minecraft:pig %s %s %s {Tags:[testpig]}", pigPos.getX(),
        pigPos.getY(), pigPos.getZ());
    Entity pig = mc().findEntities("@e[tag=testpig]").get(0);
    mc().executeCommand(
        "/lua Events.on('AttackEntityEvent'):call(function(event) print(event.target.uuid) end)");
    mc().clearEvents();

    // When:
    mc().player().leftClick(pig);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(pig.getUniqueID().toString());
  }

}
