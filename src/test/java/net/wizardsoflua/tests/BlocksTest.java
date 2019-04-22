package net.wizardsoflua.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

public class BlocksTest extends WolTestBase {
  private BlockPos posP = new BlockPos(1, 4, 1);

  @AfterEach
  public void clearBlock() {
    mc().setBlock(posP, Blocks.AIR);
  }

  // /test net.wizardsoflua.tests.BlocksTest test_get_dirt
  @Test
  public void test_get_dirt() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.AIR);

    // When:
    mc().player().chat(
        "/lua b=Blocks.get('dirt'); spell.pos=Vec3.from(%s,%s,%s); spell.block=b; print(spell.block.name)",
        posP.getX(), posP.getY(), posP.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("dirt");
  }

}
