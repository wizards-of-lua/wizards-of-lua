package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;

public class EntityJoinWorldEventTest extends WolTestBase {

  // /test net.wizardsoflua.tests.EntityJoinWorldEventTest test
  @Test
  public void test() {
    // Given:
    String customName = "testitem";
    BlockPos itemPos = mc().player().getPosition().east(5);
    mc().executeCommand("/lua " //
        + "q=Events.collect('EntityJoinWorldEvent');\n" //
        + "e=q:next();\n" //
        + "print(e.entity.name)\n" //
    );
    mc().clearEvents();

    // When:
    String customNameJson = quoteJson(toJsonString(customName));
    mc().executeCommand(
        "/summon item %s %s %s {Item:{id:anvil,Count:1},CustomName:" + customNameJson + "}",
        itemPos.getX(), itemPos.getY(), itemPos.getZ());

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("Summoned new " + customName);
    assertThat(mc().nextServerMessage()).isEqualTo(customName);
  }
}
