package net.wizardsoflua.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

public class BlockTest extends WolTestBase {
  BlockPos playerPos = new BlockPos(0, 4, 0);
  private BlockPos posP = new BlockPos(1, 4, 1);

  @BeforeEach
  public void setPlayerPos() {
    mc().player().setPosition(playerPos);
  }

  @AfterEach
  public void clearBlock() {
    mc().setBlock(posP, Blocks.AIR);
  }

  // /test net.wizardsoflua.tests.BlockTest test_block_name
  @Test
  public void test_block_name() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.STONE);

    // When:
    mc().player().chat("/lua spell.pos = Vec3.from(%s,%s,%s); b=spell.block; print(b.name)",
        posP.getX(), posP.getY(), posP.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("stone");
  }

  // /test net.wizardsoflua.tests.BlockTest test_block_classname
  @Test
  public void test_block_classname() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.OAK_PLANKS);

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
    mc().setBlock(posP, Blocks.OAK_PLANKS);

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
    mc().setBlock(posP, Blocks.OAK_PLANKS);

    // When:
    mc().player().chat("/lua spell.pos = Vec3.from(%s,%s,%s); p=spell.block.data; print(p~=nil)",
        posP.getX(), posP.getY(), posP.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.BlockTest test_block_data_of_oak_log
  @Test
  public void test_block_data_of_oak_log() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.OAK_LOG);

    // When:
    mc().player().chat("/lua spell.pos = Vec3.from(%s,%s,%s); p=spell.block.data; print(str(p))",
        posP.getX(), posP.getY(), posP.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("{\n" + "  axis = \"y\"\n" + "}");
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
    String blockId = "minecraft:oak_planks";

    // When:
    mc().player().chat(
        "/lua b=Blocks.get('furnace'):withNbt({ Items={ {Count=1, Slot=1, Damage=2, id='" + blockId
            + "' } } }); spell.pos=Vec3.from(%s,%s,%s); spell.block=b; print(spell.block.nbt.Items[1].id)",
        posP.getX(), posP.getY(), posP.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(blockId);
  }

  // /test net.wizardsoflua.tests.BlockTest test_withData_oak_log_axis_z
  @Test
  public void test_withData_oak_log_axis_z() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.AIR);

    // When:
    mc().player().chat(
        "/lua b=Blocks.get('oak_log'):withData({axis='z'}); spell.pos=Vec3.from(%s,%s,%s); spell.block=b; print(spell.block.data.axis)",
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
    ItemStack itemStack = new ItemStack(Items.APPLE);
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
