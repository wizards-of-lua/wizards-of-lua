package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

/**
 * Testing the "/wol spell break" command
 */
@RunWith(MinecraftJUnitRunner.class)
public class WolSpellBreakTest extends WolTestBase {

  // /test net.wizardsoflua.tests.WolSpellBreakTest test_spell_break__Executed_by_Server
  @Test
  public void test_spell_break__Executed_by_Server() throws Exception {
    // Given:
    String code = "while true do sleep(20); end";
    String serverCode = "--[[server]] " + code;
    String clientCode = "print('client'); " + code;
    mc().executeCommand("/lua %s", serverCode);
    mc().player().chat("/lua %s", clientCode);
    assertThat(mc().waitFor(TestPlayerReceivedChatEvent.class).getMessage()).isEqualTo("client");

    // When:
    mc().executeCommand("/wol spell break");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("[WoL] Broke 1 spell");
    Iterable<SpellEntity> spells = mc().spells();
    assertThat(spells).hasSize(1);
    assertThat(spells.iterator().next().getProgram().getCode()).isEqualTo(clientCode);
  }

  // /test net.wizardsoflua.tests.WolSpellBreakTest test_spell_break__Executed_by_Player
  @Test
  public void test_spell_break__Executed_by_Player() throws Exception {
    // Given:
    String code = "while true do sleep(20); end";
    String serverCode = "--[[server]] " + code;
    String clientCode = "--[[client]] " + code;
    mc().executeCommand("/lua %s", serverCode);
    mc().player().chat("/lua %s", clientCode);

    // When:
    mc().player().chat("/wol spell break");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("[WoL] Broke 1 spell");
    Iterable<SpellEntity> spells = mc().spells();
    assertThat(spells).hasSize(1);
    assertThat(spells.iterator().next().getProgram().getCode()).isEqualTo(serverCode);
  }

  // /test net.wizardsoflua.tests.WolSpellBreakTest test_spell_break_by_owner
  @Test
  public void test_spell_break_by_owner() throws Exception {
    // Given:
    String code = "while true do sleep(20); end";
    String serverCode = "--[[server]] " + code;
    String clientCode = "--[[client]] " + code;
    mc().executeCommand("/lua %s", serverCode);
    mc().player().chat("/lua %s", clientCode);

    // When:
    mc().player().chat("/wol spell break byOwner %s", mc().player().getName());

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("[WoL] Broke 1 spell");
    Iterable<SpellEntity> spells = mc().spells();
    assertThat(spells).hasSize(1);
    assertThat(spells.iterator().next().getProgram().getCode()).isEqualTo(serverCode);
  }

  // /test net.wizardsoflua.tests.WolSpellBreakTest test_spell_break_all
  @Test
  public void test_spell_break_all() throws Exception {
    // Given:
    String code = "while true do sleep(20); end";
    String serverCode = "--[[server]] " + code;
    String clientCode = "--[[client]] " + code;
    mc().executeCommand("/lua %s", serverCode);
    mc().player().chat("/lua %s", clientCode);

    // When:
    mc().player().chat("/wol spell break all");

    // Then:
    assertThat(mc().nextServerMessage()).startsWith("[WoL] Broke 2 spells");
    assertThat(mc().spells()).isEmpty();
  }

  // /test net.wizardsoflua.tests.WolSpellBreakTest test_spell_break_by_sid
  @Test
  public void test_spell_break_by_sid() throws Exception {
    // Given:
    String code = "print(spell.sid); while true do sleep(20); end";
    mc().executeCommand("/lua %s", code);
    mc().executeCommand("/lua %s", code);
    String sid = mc().nextServerMessage();
    mc().nextServerMessage();

    // When:
    mc().executeCommand("/wol spell break bySid %s", sid);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("[WoL] Broke 1 spell");
    assertThat(mc().spells()).hasSize(1);
  }

  // /test net.wizardsoflua.tests.WolSpellBreakTest test_spell_break_by_name
  @Test
  public void test_spell_break_by_name() throws Exception {
    // Given:
    String code = "print(spell.name); while true do sleep(20); end";
    mc().executeCommand("/lua %s", code);
    mc().executeCommand("/lua %s", code);
    String name = mc().nextServerMessage();
    mc().nextServerMessage();

    // When:
    mc().executeCommand("/wol spell break byName %s", name);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("[WoL] Broke 1 spell");
    assertThat(mc().spells()).hasSize(1);
  }

}
