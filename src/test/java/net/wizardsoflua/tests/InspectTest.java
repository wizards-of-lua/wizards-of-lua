package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

/**
 * Testing the Inspect Lua module
 */
@RunWith(MinecraftJUnitRunner.class)
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
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
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
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.InspectTest test_inspect_table
  @Test
  public void test_inspect_table() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua t={a=1,b='hello'}; print(inspect(t))");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("{\n  [\"a\"] = 1,\n  [\"b\"] = \"hello\"\n}");
  }

}
