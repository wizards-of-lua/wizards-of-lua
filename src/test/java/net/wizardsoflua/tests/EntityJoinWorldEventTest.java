package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

public class EntityJoinWorldEventTest extends WolTestBase {
  BlockPos itemPos = mc().player().getBlockPos().east(5);

  // /test net.wizardsoflua.tests.EntityJoinWorldEventTest test
  @Test
  public void test() {
    // Given:
    String expected = "testitem";
    mc().executeCommand(
        "/lua q=Events.collect('EntityJoinWorldEvent'); e=q:next(); print(e.entity.name)");
    mc().clearEvents();

    // When:
    mc().executeCommand("/summon Item %s %s %s {Item:{id:anvil,Count:1},CustomName:%s}",
        itemPos.getX(), itemPos.getY(), itemPos.getZ(), expected);

    // Then:
    ServerLog4jEvent act1 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act1.getMessage()).isEqualTo("Object successfully summoned");
    ServerLog4jEvent act2 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act2.getMessage()).isEqualTo(expected);
  }

}
