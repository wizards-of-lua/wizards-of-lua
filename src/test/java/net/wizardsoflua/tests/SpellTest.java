package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;
import net.wizardsoflua.testenv.net.ChatAction;

@RunWith(MinecraftJUnitRunner.class)
public class SpellTest extends WolTestBase {

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

  // /test net.wizardsoflua.tests.SpellTest test_spell_can_be_found_with_selector
  @Test
  public void test_spell_can_be_found_with_selector() throws Exception {
    // Given:
    mc().executeCommand("/lua for i=1,10 do sleep(20); end");

    // When:
    mc().executeCommand("/execute @e[type=wol:Spell] 0 0 0 say hi");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).matches("\\[Spell-\\d+\\] hi");
  }

  // /test net.wizardsoflua.tests.SpellTest test_spell_execute_command_casted_by_server
  @Test
  public void test_spell_execute_command_casted_by_server() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua spell:execute('/say hi')");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).matches("\\[Spell-\\d+\\] hi");
  }

  // /test net.wizardsoflua.tests.SpellTest test_spell_execute_command_casted_by_player
  @Test
  public void test_spell_execute_command_casted_by_player() throws Exception {
    // Given:

    // When:
    mc().player().perform(new ChatAction("/lua spell:execute('/say ho')"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    String message = act.getMessage();
    assertThat(message).matches("\\[Spell-\\d+\\] ho");
  }
}
