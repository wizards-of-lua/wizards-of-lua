package net.wizardsoflua.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

public class MaterialTest extends WolTestBase {
  private BlockPos posP = new BlockPos(1, 4, 1);

  @AfterEach
  public void clearBlock() {
    mc().setBlock(posP, Blocks.AIR);
  }

  // /test net.wizardsoflua.tests.MaterialTest test_material_classname
  @Test
  public void test_material_classname() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.OAK_PLANKS);

    // When:
    mc().player().chat(
        "/lua spell.pos = Vec3.from(%s,%s,%s); m=spell.block.material; cls=type(m); print(cls)",
        posP.getX(), posP.getY(), posP.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("Material");
  }

  // /test net.wizardsoflua.tests.MaterialTest test_material_of_planks
  @Test
  public void test_material_of_planks() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.OAK_PLANKS);
    String expected = "{\n" //
        + "  blocksMovement = true,\n" //
        + "  canBurn = true,\n" //
        + "  liquid = false,\n" //
        + "  mobility = \"NORMAL\",\n" //
        + "  name = \"WOOD\",\n" //
        + "  opaque = true,\n" //
        + "  replaceable = false,\n" //
        + "  requiresNoTool = true,\n" //
        + "  solid = true\n" //
        + "}";
    // When:
    mc().player().chat(
        "/lua spell.pos = Vec3.from(%s,%s,%s); m=spell.block.material; print(str(m))", posP.getX(),
        posP.getY(), posP.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.MaterialTest test_material_is_cached
  @Test
  public void test_material_is_cached() throws Exception {
    // Given:

    // When:
    mc().player().chat(
        "/lua m1=Blocks.get('stone').material; m2=Blocks.get('stone').material; print(m1==m2)");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }
}
