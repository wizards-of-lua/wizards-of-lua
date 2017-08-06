package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

/**
 * Testing the Check Lua module
 */
@RunWith(MinecraftJUnitRunner.class)
public class CheckTest extends WolTestBase {

  // /test net.wizardsoflua.tests.CheckTest test_check_number_with_number
  @Test
  public void test_check_number_with_number() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua Check.isNumber(1); print('ok')");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
  }

  // /test net.wizardsoflua.tests.CheckTest test_check_number_with_not_a_number
  @Test
  public void test_check_number_with_not_a_number() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua Check.isNumber('xxx')");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).contains("number expected");
  }

  // /test net.wizardsoflua.tests.CheckTest test_check_string_with_string
  @Test
  public void test_check_string_with_string() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua Check.isString('abc'); print('ok')");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
  }

  // /test net.wizardsoflua.tests.CheckTest test_check_string_with_not_a_string
  @Test
  public void test_check_string_with_not_a_string() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua Check.isString({})");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).contains("string expected");
  }

}
