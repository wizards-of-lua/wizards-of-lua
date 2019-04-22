package net.wizardsoflua.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

/**
 * Testing the "/wol luaTicksLimit" command
 */
public class WolLuaTicksLimitTest extends WolTestBase {

  @AfterEach
  public void resetTicksLimit() {
    mc().setLuaTicksLimit(10000);
  }

  // /test net.wizardsoflua.tests.WolLuaTicksLimitTest test_luatickslimit_returns_current_value
  @Test
  public void test_luatickslimit_returns_current_value() throws Exception {
    // Given:
    mc().setLuaTicksLimit(5555);
    String expected = "[WoL] luaTicksLimit = 5555";

    // When:
    mc().executeCommand("/wol luaTicksLimit");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WolLuaTicksLimitTest test_luatickslimit_returns_current_value
  @Test
  public void test_luatickslimit_set_modifies_value() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/wol luaTicksLimit set 5555");

    // Then:
    mc().waitFor(ServerLog4jEvent.class);
    assertThat(mc().getLuaTicksLimit()).isEqualTo(5555);
  }

}
