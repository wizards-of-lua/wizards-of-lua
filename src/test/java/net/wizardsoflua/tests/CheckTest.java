package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.wizardsoflua.testenv.WolTestBase;

/**
 * Testing the Check Lua module
 */
public class CheckTest extends WolTestBase {

  // /test net.wizardsoflua.tests.CheckTest test_check_number_with_number
  @Test
  public void test_check_number_with_number() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua Check.isNumber(1); print('ok')");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("ok");
  }

  // /test net.wizardsoflua.tests.CheckTest test_check_number_with_not_a_number
  @Test
  public void test_check_number_with_not_a_number() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua Check.isNumber('xxx')");

    // Then:
    assertThat(mc().nextServerMessage()).contains("number expected");
  }

  // /test net.wizardsoflua.tests.CheckTest test_check_string_with_string
  @Test
  public void test_check_string_with_string() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua Check.isString('abc'); print('ok')");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("ok");
  }

  // /test net.wizardsoflua.tests.CheckTest test_check_string_with_not_a_string
  @Test
  public void test_check_string_with_not_a_string() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua Check.isString({})");

    // Then:
    assertThat(mc().nextServerMessage()).contains("string expected");
  }

}
