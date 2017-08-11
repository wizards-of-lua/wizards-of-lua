package net.wizardsoflua.tests;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;
import net.wizardsoflua.testenv.net.ChatAction;

@RunWith(MinecraftJUnitRunner.class)
public class SpellTest extends WolTestBase {
  private BlockPos posP = new BlockPos(1, 4, 1);

  @After
  public void clearBlock() {
    mc().setBlock(posP, Blocks.AIR);
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

  // /test net.wizardsoflua.tests.SpellTest test_spell_block
  @Test
  public void test_spell_block() throws Exception {
    // Given:
    mc().setBlock(posP, Blocks.DIAMOND_ORE);

    // When:
    mc().executeCommand("/lua b=spell.block; print(b.name)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("minecraft:diamond_ore");
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
