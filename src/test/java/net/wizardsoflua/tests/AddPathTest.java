package net.wizardsoflua.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import net.wizardsoflua.testenv.WolTestBase;

public class AddPathTest extends WolTestBase {

  private static final String DEMOMODULE = "my.demomodule";

  @AfterEach
  public void after() {
    mc().player().deleteModule(DEMOMODULE);
  }

  // /test net.wizardsoflua.tests.AddPathTest test_server_can_call_player_module
  @Test
  public void test_server_can_call_player_module() throws Exception {
    // Given:
    mc().player().createModule(DEMOMODULE, "function dummy() print('hello') end");

    // When:
    mc().executeCommand("/lua addpath('%s'); require('%s'); dummy()", mc().player().getName(),
        DEMOMODULE);

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("hello");
  }

}
