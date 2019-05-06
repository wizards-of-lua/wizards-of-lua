package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.testenv.WolTestBase;

public class WolTest extends WolTestBase {

  // /test net.wizardsoflua.tests.WolTest test_version
  @Test
  public void test_version() throws Exception {
    // Given:
    String expected = WizardsOfLua.VERSION;

    // When:
    mc().executeCommand("/lua print(Wol.version)");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo(expected);
  }

}
