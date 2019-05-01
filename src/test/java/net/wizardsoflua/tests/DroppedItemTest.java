package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

public class DroppedItemTest extends WolTestBase {

  // /test net.wizardsoflua.tests.DroppedItemTest test_anvil_item_instanceOf_DroppedEntity
  @Test
  public void test_anvil_item_instanceOf_DroppedEntity() throws Exception {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().executeCommand("/summon item %s %s %s {Item:{id:anvil,Count:1},Tags:[testitem]}",
        pos.getX(), pos.getY(), pos.getZ());
    mc().clearEvents();

    // When:
    mc().executeCommand(
        "/lua p=Entities.find('@e[tag=testitem]')[1]; print(instanceOf(DroppedItem,p))");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }

}
