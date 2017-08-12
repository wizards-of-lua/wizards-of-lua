package net.wizardsoflua.tests;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;
import net.wizardsoflua.testenv.net.ChatAction;

@RunWith(MinecraftJUnitRunner.class)
public class SpellTest extends WolTestBase {
  private BlockPos posP1 = new BlockPos(1, 4, 1);
  private BlockPos posP2 = new BlockPos(1, 4, -1);
  private BlockPos posP3 = new BlockPos(-1, 4, 1);
  private BlockPos posP4 = new BlockPos(-1, 4, -1);

  @After
  public void clearBlock() {
    mc().setBlock(posP1, Blocks.AIR);
    mc().setBlock(posP2, Blocks.AIR);
    mc().setBlock(posP3, Blocks.AIR);
    mc().setBlock(posP4, Blocks.AIR);
  }

  // /test net.wizardsoflua.tests.SpellTest test_spell_is_not_nil
  @Test
  public void test_spell_is_not_nil() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua print(spell~=nil)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.SpellTest test_spell_pos_is_world_spawn_point_casted_by_server
  @Test
  public void test_spell_pos_is_world_spawn_point_casted_by_server() throws Exception {
    // Given:
    BlockPos spawnPoint = mc().getWorldSpawnPoint();
    String expected = format(spawnPoint);

    // When:
    mc().executeCommand("/lua p=spell.pos; print(p)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellTest test_spell_pos_when_casted_by_player
  @Test
  public void test_spell_pos_when_casted_by_player() throws Exception {
    // Given:
    Vec3d lookPoint = mc().player().getPositionLookingAt();
    String expected = format(lookPoint);

    // When:
    mc().player().perform(new ChatAction("/lua p=spell.pos; print(p)"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellTest test_spell_block_1
  @Test
  public void test_spell_block_1() throws Exception {
    // Given:
    mc().setBlock(posP1, Blocks.DIAMOND_ORE);

    // When:
    mc().player().perform(new ChatAction("/lua spell.pos = Vec3.from(%s,%s,%s); b=spell.block; print(b.name)",
        posP1.getX(), posP1.getY(), posP1.getZ()));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("diamond_ore");
  }

  // /test net.wizardsoflua.tests.SpellTest test_spell_block_2
  @Test
  public void test_spell_block_2() throws Exception {
    // Given:
    mc().setBlock(posP2, Blocks.DIAMOND_ORE);

    // When:
    mc().player().perform(new ChatAction("/lua spell.pos = Vec3.from(%s,%s,%s); b=spell.block; print(b.name)",
        posP2.getX(), posP2.getY(), posP2.getZ()));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("diamond_ore");
  }

  // /test net.wizardsoflua.tests.SpellTest test_spell_block_3
  @Test
  public void test_spell_block_3() throws Exception {
    // Given:
    mc().setBlock(posP3, Blocks.DIAMOND_ORE);

    // When:
    mc().player().perform(new ChatAction("/lua spell.pos = Vec3.from(%s,%s,%s); b=spell.block; print(b.name)",
        posP3.getX(), posP3.getY(), posP3.getZ()));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("diamond_ore");
  }

  // /test net.wizardsoflua.tests.SpellTest test_spell_block_4
  @Test
  public void test_spell_block_4() throws Exception {
    // Given:
    mc().setBlock(posP4, Blocks.DIAMOND_ORE);

    // When:
    mc().player().perform(new ChatAction("/lua spell.pos = Vec3.from(%s,%s,%s); b=spell.block; print(b.name)",
        posP4.getX(), posP4.getY(), posP4.getZ()));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("diamond_ore");
  }

  // /test net.wizardsoflua.tests.SpellTest test_spell_owner_is_not_nil_for_player
  @Test
  public void test_spell_owner_is_not_nil_for_player() throws Exception {
    // Given:

    // When:
    mc().player().perform(new ChatAction("/lua print(spell.owner~=nil)"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.SpellTest test_spell_owner_is_nil_for_server
  @Test
  public void test_spell_owner_is_nil_for_server() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua print(spell.owner==nil)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.SpellTest test_spell_owner_uuid_is_current_player_uuid
  @Test
  public void test_spell_owner_uuid_is_current_player_uuid() throws Exception {
    // Given:
    String expected = mc().player().getDelegate().getUniqueID().toString();

    // When:
    mc().player().perform(new ChatAction("/lua print(spell.owner.uuid)"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellTest test_spell_owner_name_is_current_player_name
  @Test
  public void test_spell_owner_name_is_current_player_name() throws Exception {
    // Given:
    String expected = mc().player().getDelegate().getName();

    // When:
    mc().player().perform(new ChatAction("/lua print(spell.owner.name)"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellTest test_spell_owner_is_readonly
  @Test
  public void test_spell_owner_is_readonly() throws Exception {
    // Given:

    // When:
    mc().player().perform(new ChatAction("/lua spell.owner = nil"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).contains("Error").contains("property is readonly");
  }


}
