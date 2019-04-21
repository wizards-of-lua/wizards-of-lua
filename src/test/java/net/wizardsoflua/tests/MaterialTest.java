package net.wizardsoflua.tests;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.lua.classes.block.MaterialClass;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

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
        + "  blocksLight = true,\n" //
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

  // /test net.wizardsoflua.tests.MaterialTest test_all_material_names_are_mapped
  @Test
  public void test_all_material_names_are_mapped() throws Exception {
    // Given:
    Map<Material, String> expectedNames = new HashMap<>();
    Field[] fields = Material.class.getFields();
    for (Field field : fields) {
      int modifiers = field.getModifiers();
      if (Material.class.isAssignableFrom(field.getType())//
          && Modifier.isPublic(modifiers)//
          && Modifier.isStatic(modifiers)//
          && Modifier.isFinal(modifiers)//
      ) {
        Material material = (Material) field.get(null);
        String name = field.getName();
        expectedNames.put(material, name);
      }
    }
    // When:
    for (Map.Entry<Material, String> entry : expectedNames.entrySet()) {
      String act = MaterialClass.getName(entry.getKey());
      // Then
      assertThat(act).isEqualTo(entry.getValue());
    }
  }

}
