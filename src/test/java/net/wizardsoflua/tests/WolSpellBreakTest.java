package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

/**
 * Testing the "/wol spell break" command
 */
@RunWith(MinecraftJUnitRunner.class)
public class WolSpellBreakTest extends WolTestBase {

  // /test net.wizardsoflua.tests.WolSpellBreakTest test_spell_break_all
  @Test
  public void test_spell_break_all() throws Exception {
    // Given:
    String code = "while true do sleep(20); end";
    mc().executeCommand("/lua %s", code);
    
    // When:
    mc().executeCommand("/wol spell break all");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).startsWith("[WoL] Broke all spells.");
    assertThat(mc().spells()).isEmpty();
  }

}
