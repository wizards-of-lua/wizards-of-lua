package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

/**
 * Testing the "/wol spell list" command
 */
@RunWith(MinecraftJUnitRunner.class)
public class WolSpellListTest extends WolTestBase {

  // /test net.wizardsoflua.tests.WolSpellListTest test_spell_list
  @Test
  public void test_spell_list() throws Exception {
    // Given:
    String code = "while true do sleep(20); end";
    mc().executeCommand("/lua %s", code);
    // When:
    mc().executeCommand("/wol spell list");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).startsWith("[WoL] Active spells:\n");
    assertThat(act.getMessage().replace('\n', ' ')).matches(".*Spell-\\d+:.*");
    assertThat(act.getMessage()).contains(code);
  }

}
