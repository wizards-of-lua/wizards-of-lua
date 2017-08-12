package net.wizardsoflua.tests;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;
import net.wizardsoflua.testenv.net.ChatAction;

@RunWith(MinecraftJUnitRunner.class)
public class BlockTest extends WolTestBase {
  private BlockPos posP = new BlockPos(1, 4, 1);

  @After
  public void clearBlock() {
    mc().setBlock(posP, Blocks.AIR);
  }

  // /test net.wizardsoflua.tests.BlockTest test_block_name
  @Test
  public void test_block_name() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.PLANKS);

    // When:
    mc().player().perform(
        new ChatAction("/lua spell.pos = Vec3.from(%s,%s,%s); b=spell.block; print(b.name)",
            posP.getX(), posP.getY(), posP.getZ()));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("planks");
  }

  // /test net.wizardsoflua.tests.BlockTest test_block_classname
  @Test
  public void test_block_classname() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.PLANKS);

    // When:
    mc().player()
        .perform(new ChatAction(
            "/lua spell.pos = Vec3.from(%s,%s,%s); b=spell.block; cls=type(b); print(cls)",
            posP.getX(), posP.getY(), posP.getZ()));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("Block");
  }

  // /test net.wizardsoflua.tests.BlockTest test_block_has_material
  @Test
  public void test_block_has_material() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.PLANKS);

    // When:
    mc().player()
        .perform(new ChatAction(
            "/lua spell.pos = Vec3.from(%s,%s,%s); m=spell.block.material; print(m~=nil)",
            posP.getX(), posP.getY(), posP.getZ()));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }

}
