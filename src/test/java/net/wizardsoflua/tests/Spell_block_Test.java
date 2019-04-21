package net.wizardsoflua.tests;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

@RunWith(MinecraftJUnitRunner.class)
public class Spell_block_Test extends WolTestBase {
  private BlockPos posP1 = new BlockPos(1, 4, 1);
  private BlockPos posP2 = new BlockPos(1, 4, -1);
  private BlockPos posP3 = new BlockPos(-1, 4, 1);
  private BlockPos posP4 = new BlockPos(-1, 4, -1);

  private BlockPos lowerDoorPos = new BlockPos(1, 4, 1);
  private BlockPos upperDoorPos = new BlockPos(1, 5, 1);

  @After
  public void clearBlock() {
    mc().setBlock(posP1, Blocks.AIR);
    mc().setBlock(posP2, Blocks.AIR);
    mc().setBlock(posP3, Blocks.AIR);
    mc().setBlock(posP4, Blocks.AIR);
    mc().setBlock(lowerDoorPos, Blocks.AIR);
    mc().setBlock(upperDoorPos, Blocks.AIR);
  }

  // /test net.wizardsoflua.tests.Spell_block_Test test_spell_block_1
  @Test
  public void test_spell_block_1() throws Exception {
    // Given:
    mc().setBlock(posP1, Blocks.DIAMOND_ORE);

    // When:
    mc().player().chat("/lua spell.pos = Vec3.from(%s,%s,%s); b=spell.block; print(b.name)",
        posP1.getX(), posP1.getY(), posP1.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("diamond_ore");
  }

  // /test net.wizardsoflua.tests.Spell_block_Test test_spell_block_2
  @Test
  public void test_spell_block_2() throws Exception {
    // Given:
    mc().setBlock(posP2, Blocks.DIAMOND_ORE);

    // When:
    mc().player().chat("/lua spell.pos = Vec3.from(%s,%s,%s); b=spell.block; print(b.name)",
        posP2.getX(), posP2.getY(), posP2.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("diamond_ore");
  }

  // /test net.wizardsoflua.tests.Spell_block_Test test_spell_block_3
  @Test
  public void test_spell_block_3() throws Exception {
    // Given:
    mc().setBlock(posP3, Blocks.DIAMOND_ORE);

    // When:
    mc().player().chat("/lua spell.pos = Vec3.from(%s,%s,%s); b=spell.block; print(b.name)",
        posP3.getX(), posP3.getY(), posP3.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("diamond_ore");
  }

  // /test net.wizardsoflua.tests.Spell_block_Test test_spell_block_4
  @Test
  public void test_spell_block_4() throws Exception {
    // Given:
    mc().setBlock(posP4, Blocks.DIAMOND_ORE);

    // When:
    mc().player().chat("/lua spell.pos = Vec3.from(%s,%s,%s); b=spell.block; print(b.name)",
        posP4.getX(), posP4.getY(), posP4.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("diamond_ore");
  }

  // /test net.wizardsoflua.tests.Spell_block_Test test_spell_block_can_place_door
  @Test
  public void test_spell_block_can_place_door() throws Exception {
    // Given:
    mc().setBlock(lowerDoorPos, Blocks.AIR);
    mc().setBlock(upperDoorPos, Blocks.AIR);

    // When:
    mc().executeCommand(
        "/lua spell.pos = Vec3.from(%s,%s,%s); spell.block=Blocks.get('wooden_door'):withData({half='lower'}); spell:move('up'); spell.block=Blocks.get('wooden_door'):withData({half='upper'}); print('ok')",
        posP1.getX(), posP1.getY(), posP1.getZ());

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");

    IBlockState actLower = mc().getBlock(lowerDoorPos);
    assertThat(actLower).isA(Blocks.OAK_DOOR).property(BlockDoor.HALF)
        .isEqualTo(DoubleBlockHalf.LOWER);
    IBlockState actUpper = mc().getBlock(upperDoorPos);
    assertThat(actUpper).isA(Blocks.OAK_DOOR).property(BlockDoor.HALF)
        .isEqualTo(DoubleBlockHalf.UPPER);
  }

  // /test net.wizardsoflua.tests.Spell_block_Test test_block_withNbt
  @Test
  public void test_block_withNbt() throws Exception {
    // Given:
    mc().setBlock(posP1, Blocks.FURNACE);

    // When:
    mc().player().chat(
        "/lua b=Blocks.get('furnace'):withNbt({ Items={ {Count=1, Slot=1, Damage=2, id='minecraft:planks' } } }); spell.pos=Vec3.from(%s,%s,%s); spell.block=b; print(spell.block.nbt.Items[1].id)",
        posP1.getX(), posP1.getY(), posP1.getZ());

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("minecraft:planks");
  }


}
