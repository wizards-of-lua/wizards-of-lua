package net.wizardsoflua.tests;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

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
    mc().player().chat("/lua spell.pos = Vec3.from(%s,%s,%s); b=spell.block; print(b.name)",
        posP.getX(), posP.getY(), posP.getZ());

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
    mc().player().chat(
        "/lua spell.pos = Vec3.from(%s,%s,%s); b=spell.block; cls=type(b); print(cls)", posP.getX(),
        posP.getY(), posP.getZ());

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
    mc().player().chat(
        "/lua spell.pos = Vec3.from(%s,%s,%s); m=spell.block.material; print(m~=nil)", posP.getX(),
        posP.getY(), posP.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.BlockTest test_block_has_data
  @Test
  public void test_block_has_data() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.PLANKS);

    // When:
    mc().player().chat("/lua spell.pos = Vec3.from(%s,%s,%s); p=spell.block.data; print(p~=nil)",
        posP.getX(), posP.getY(), posP.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.BlockTest test_block_data_of_planks
  @Test
  public void test_block_data_of_planks() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.PLANKS);
    String expected = "{\n" + "  variant = \"oak\"\n" + "}";
    // When:
    mc().player().chat("/lua spell.pos = Vec3.from(%s,%s,%s); p=spell.block.data; print(str(p))",
        posP.getX(), posP.getY(), posP.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.BlockTest test_withData_furnace_facing_east
  @Test
  public void test_withData_furnace_facing_east() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.AIR);

    // When:
    mc().player().chat(
        "/lua b=Blocks.get('furnace'):withData({facing='east'}); spell.pos=Vec3.from(%s,%s,%s); spell.block=b; print(spell.block.data.facing)",
        posP.getX(), posP.getY(), posP.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("east");
  }

  // /test net.wizardsoflua.tests.BlockTest test_withNbt_furnace_having_planks_in_slot_1
  @Test
  public void test_withNbt_furnace_having_planks_in_slot_1() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.AIR);

    // When:
    mc().player().chat(
        "/lua b=Blocks.get('furnace'):withNbt({ Items={ {Count=1, Slot=1, Damage=2, id='minecraft:planks' } } }); spell.pos=Vec3.from(%s,%s,%s); spell.block=b; print(spell.block.nbt.Items[1].id)",
        posP.getX(), posP.getY(), posP.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("minecraft:planks");
  }

  // /test net.wizardsoflua.tests.BlockTest test_withData_log_variant_oak
  @Test
  public void test_withData_log_variant_oak() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.AIR);

    // When:
    mc().player().chat(
        "/lua b=Blocks.get('log'):withData({variant='oak'}); spell.pos=Vec3.from(%s,%s,%s); spell.block=b; print(spell.block.data.variant)",
        posP.getX(), posP.getY(), posP.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("oak");
  }

  // /test net.wizardsoflua.tests.BlockTest test_withData_log_axis_z
  @Test
  public void test_withData_log_axis_z() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.AIR);

    // When:
    mc().player().chat(
        "/lua b=Blocks.get('log'):withData({axis='z'}); spell.pos=Vec3.from(%s,%s,%s); spell.block=b; print(spell.block.data.axis)",
        posP.getX(), posP.getY(), posP.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("z");
  }

  // /test net.wizardsoflua.tests.BlockTest test_asItem_from_dirt
  @Test
  public void test_asItem_from_dirt() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.DIRT);
    String expected = "Dirt";

    // When:
    mc().player().chat(
        "/lua spell.pos=Vec3.from(%s,%s,%s); i=spell.block:asItem(); print(i.displayName)",
        posP.getX(), posP.getY(), posP.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.BlockTest test_asItem_from_empty_chest
  @Test
  public void test_asItem_from_empty_chest() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.CHEST);
    String expected = "{}";

    // When:
    mc().player().chat(
        "/lua spell.pos=Vec3.from(%s,%s,%s); i=spell.block:asItem(); print( str(i.nbt.tag.BlockEntityTag.Items))",
        posP.getX(), posP.getY(), posP.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.BlockTest test_asItem_from_filled_chest
  @Test
  public void test_asItem_from_filled_chest() throws Exception {
    // Given:
    ItemStack itemStack = mc().getItemStack(Items.APPLE);
    mc().setChest(posP, itemStack);
    String expected = itemStack.getItem().getRegistryName().toString();

    // When:
    mc().player().chat(
        "/lua spell.pos=Vec3.from(%s,%s,%s); i=spell.block:asItem(); print( i.nbt.tag.BlockEntityTag.Items[1].id)",
        posP.getX(), posP.getY(), posP.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

}
