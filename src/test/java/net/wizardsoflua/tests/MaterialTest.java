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
public class MaterialTest extends WolTestBase {
  private BlockPos posP = new BlockPos(1, 4, 1);

  @After
  public void clearBlock() {
    mc().setBlock(posP, Blocks.AIR);
  }



  // /test net.wizardsoflua.tests.MaterialTest test_material_classname
  @Test
  public void test_material_classname() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.PLANKS);

    // When:
    mc().player()
        .perform(new ChatAction(
            "/lua spell.pos = Vec3.from(%s,%s,%s); m=spell.block.material; cls=type(m); print(cls)",
            posP.getX(), posP.getY(), posP.getZ()));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("Material");
  }

  // /test net.wizardsoflua.tests.MaterialTest test_material_of_planks
  @Test
  public void test_material_of_planks() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.PLANKS);
    String expected = "{\n" //
        + "  blocksLight = true,\n" //
        + "  blocksMovement = true,\n" //
        + "  canBurn = true,\n" //
        + "  isLiquid = false,\n" //
        + "  isOpaque = true,\n" //
        + "  isSolid = true,\n" //
        + "  isToolNotRequired = true,\n" //
        + "  mobility = \"NORMAL\"\n" //
        + "}";
    // When:
    mc().player()
        .perform(new ChatAction(
            "/lua spell.pos = Vec3.from(%s,%s,%s); m=spell.block.material; print(str(m))",
            posP.getX(), posP.getY(), posP.getZ()));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

}
