package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.wizardsoflua.testenv.WolTestBase;

public class GlobalsTest extends WolTestBase {

  // /test net.wizardsoflua.tests.GlobalsTest test_str
  @Test
  public void test_str() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua t={a=1,b='hello'}; print(str(t))");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("{\n  [\"a\"] = 1,\n  [\"b\"] = \"hello\"\n}");
  }

  // /test net.wizardsoflua.tests.GlobalsTest test_sleep
  @Test
  public void test_sleep() throws Exception {
    // Given:
    long sleepTime = 10;
    // When:
    mc().executeCommand("/lua print(Time.gametime); sleep(%s); print(Time.gametime)", sleepTime);

    // Then:
    String message1 = mc().nextServerMessage();
    String message2 = mc().nextServerMessage();
    long actual = Long.parseLong(message2) - Long.parseLong(message1);
    assertThat(actual).isEqualTo(sleepTime);
  }

}
