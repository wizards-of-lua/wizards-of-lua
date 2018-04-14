package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class WolTest extends WolTestBase {

  // /test net.wizardsoflua.tests.WolTest test_version
  @Test
  public void test_version() throws Exception {
    // Given:
    String expected = WizardsOfLua.VERSION;

    // When:
    mc().executeCommand("/lua print(Wol.version)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

}
