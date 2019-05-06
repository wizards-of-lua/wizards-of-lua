package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.wizardsoflua.testenv.WolTestBase;

/**
 * Testing the Inspect Lua module
 */
public class InspectTest extends WolTestBase {

  // /test net.wizardsoflua.tests.InspectTest test_inspect_number
  @Test
  public void test_inspect_number() throws Exception {
    // Given:
    int n = 5;
    String expected = String.valueOf(n);

    // When:
    mc().executeCommand("/lua print(inspect(%s))", n);

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.InspectTest test_inspect_string
  @Test
  public void test_inspect_string() throws Exception {
    // Given:
    String str = "hello";
    String expected = "\"" + str + "\"";

    // When:
    mc().executeCommand("/lua print(inspect('%s'))", str);

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.InspectTest test_inspect_table
  @Test
  public void test_inspect_table() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua t={a=1,b='hello'}; print(inspect(t))");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("{\n  [\"a\"] = 1,\n  [\"b\"] = \"hello\"\n}");
  }

}
