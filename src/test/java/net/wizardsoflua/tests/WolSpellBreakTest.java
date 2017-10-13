package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

/**
 * Testing the "/wol spell break" command
 */
@RunWith(MinecraftJUnitRunner.class)
public class WolSpellBreakTest extends WolTestBase {

  // /test net.wizardsoflua.tests.WolSpellBreakTest test_spell_break_all
  @Test
  public void test_spell_break_all() throws Exception {
    // Given:
    String code = "while true do sleep(100); end";
    mc().executeCommand("/lua %s", code);

    // When:
    mc().executeCommand("/wol spell break all");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).startsWith("[WoL] Broke all spells");
    assertThat(mc().spells()).isEmpty();
  }

  // /test net.wizardsoflua.tests.WolSpellBreakTest test_spell_break_by_sid
  @Test
  public void test_spell_break_by_sid() throws Exception {
    // Given:
    String code = "print(spell.sid); while true do sleep(100); end";
    mc().executeCommand("/lua %s", code);
    ServerLog4jEvent evt = mc().waitFor(ServerLog4jEvent.class);
    String sid = evt.getMessage();

    // When:
    mc().executeCommand("/wol spell break bySid %s", sid);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("[WoL] Broke 1 spell");
    assertThat(mc().spells()).isEmpty();
  }

  // /test net.wizardsoflua.tests.WolSpellBreakTest test_spell_break_by_name
  @Test
  public void test_spell_break_by_name() throws Exception {
    // Given:
    String code = "print(spell.name); while true do sleep(100); end";
    mc().executeCommand("/lua %s", code);
    ServerLog4jEvent evt = mc().waitFor(ServerLog4jEvent.class);
    String name = evt.getMessage();

    // When:
    mc().executeCommand("/wol spell break byName %s", name);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("[WoL] Broke 1 spell");
    assertThat(mc().spells()).isEmpty();
  }

  // /test net.wizardsoflua.tests.WolSpellBreakTest test_spell_break_by_owner
  @Test
  public void test_spell_break_by_owner() throws Exception {
    // Given:
    String code = "print(spell.owner.name); while true do sleep(100); end";
    mc().player().chat("/lua %s", code);
    TestPlayerReceivedChatEvent evt = mc().waitFor(TestPlayerReceivedChatEvent.class);
    String ownerName = evt.getMessage();

    // When:
    mc().executeCommand("/wol spell break byOwner %s", ownerName);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("[WoL] Broke 1 spell");
    assertThat(mc().spells()).isEmpty();
  }

}
