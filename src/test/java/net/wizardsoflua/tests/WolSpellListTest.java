package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

/**
 * Testing the "/wol spell list" command
 */
public class WolSpellListTest extends WolTestBase {
  private static final int MAX_LENGTH = 40;

  // /test net.wizardsoflua.tests.WolSpellListTest test_spell_list__Executed_by_Server
  @Test
  public void test_spell_list__Executed_by_Server() throws Exception {
    // Given:
    String code = "while true do sleep(20); end";
    String clientCode = "print('client'); " + code;
    String serverCode = "--[[server]] " + code;
    mc().player().chat("/lua %s", clientCode);
    mc().executeCommand("/lua %s", serverCode);
    assertThat(mc().waitFor(TestPlayerReceivedChatEvent.class).getMessage()).isEqualTo("client");

    // When:
    mc().executeCommand("/wol spell list");

    // Then:
    String actual = mc().nextServerMessage();
    assertThat(actual).startsWith("[WoL] Your active spells:\n");
    assertThat(actual).doesNotContain(clientCode.substring(0, MAX_LENGTH));
    assertThat(actual).contains(serverCode.substring(0, MAX_LENGTH));
  }

  // /test net.wizardsoflua.tests.WolSpellListTest test_spell_list__Executed_by_Player
  @Test
  public void test_spell_list__Executed_by_Player() throws Exception {
    // Given:
    String code = "while true do sleep(20); end";
    String clientCode = "--[[client]] " + code;
    String serverCode = "--[[server]] " + code;
    mc().player().chat("/lua %s", clientCode);
    mc().executeCommand("/lua %s", serverCode);

    // When:
    mc().player().chat("/wol spell list");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).startsWith("[WoL] Your active spells:\n");
    assertThat(act.getMessage()).contains(clientCode.substring(0, MAX_LENGTH));
    assertThat(act.getMessage()).doesNotContain(serverCode.substring(0, MAX_LENGTH));
  }

  // /test net.wizardsoflua.tests.WolSpellListTest test_spell_list_by_owner
  @Test
  public void test_spell_list_by_owner() throws Exception {
    // Given:
    String code = "while true do sleep(20); end";
    String clientCode = "--[[client]] " + code;
    String serverCode = "--[[server]] " + code;
    mc().player().chat("/lua %s", clientCode);
    mc().executeCommand("/lua %s", serverCode);
    String owner = mc().player().getName();

    // When:
    mc().player().chat("/wol spell list byOwner %s", owner);

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).startsWith("[WoL] Active spells of " + owner + ":\n");
    assertThat(act.getMessage()).contains(clientCode.substring(0, MAX_LENGTH));
    assertThat(act.getMessage()).doesNotContain(serverCode.substring(0, MAX_LENGTH));
  }

  // /test net.wizardsoflua.tests.WolSpellListTest test_spell_list_all
  @Test
  public void test_spell_list_all() throws Exception {
    // Given:
    String code = "while true do sleep(20); end";
    String clientCode = "print('client'); " + code;
    String serverCode = "--[[server]] " + code;
    mc().player().chat("/lua %s", clientCode);
    mc().executeCommand("/lua %s", serverCode);
    assertThat(mc().nextClientMessage()).isEqualTo("client");

    // When:
    mc().executeCommand("/wol spell list all");

    // Then:
    String actual = mc().nextServerMessage();
    assertThat(actual).startsWith("[WoL] Active spells:\n");
    assertThat(actual).contains(clientCode.substring(0, MAX_LENGTH));
    assertThat(actual).contains(serverCode.substring(0, MAX_LENGTH));
  }

  // /test net.wizardsoflua.tests.WolSpellListTest test_spell_list_by_sid
  @Test
  public void test_spell_list_by_sid() throws Exception {
    // Given:
    String code = "print(spell.sid); while true do sleep(100); end";
    String clientCode = "--[[client]] " + code;
    String serverCode = "--[[server]] " + code;
    mc().player().chat("/lua %s", clientCode);
    mc().executeCommand("/lua %s", serverCode);
    String sid = mc().nextClientMessage();

    // When:
    mc().executeCommand("/wol spell list bySid %s", sid);

    // Then:
    String actual = mc().nextServerMessage();
    assertThat(actual).startsWith("[WoL] Active spells with sid " + sid + ":\n");
    assertThat(actual).contains(clientCode.substring(0, MAX_LENGTH));
    assertThat(actual).doesNotContain(serverCode.substring(0, MAX_LENGTH));
  }

  // /test net.wizardsoflua.tests.WolSpellListTest test_spell_list_by_name
  @Test
  public void test_spell_list_by_name() throws Exception {
    // Given:
    String code = "print(spell.name); while true do sleep(100); end";
    String clientCode = "--[[client]] " + code;
    String serverCode = "--[[server]] " + code;
    mc().player().chat("/lua %s", clientCode);
    mc().executeCommand("/lua %s", serverCode);
    String name = mc().nextClientMessage();

    // When:
    mc().executeCommand("/wol spell list byName %s", name);

    // Then:
    String actual = mc().nextServerMessage();
    assertThat(actual).startsWith("[WoL] Active spells with name '" + name + "':\n");
    assertThat(actual).contains(clientCode.substring(0, MAX_LENGTH));
    assertThat(actual).doesNotContain(serverCode.substring(0, MAX_LENGTH));
  }

}
