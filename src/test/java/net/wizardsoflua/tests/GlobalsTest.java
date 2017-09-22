package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class GlobalsTest extends WolTestBase {

  // /test net.wizardsoflua.tests.GlobalsTest test_str
  @Test
  public void test_str() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua t={a=1,b='hello'}; print(str(t))");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("{\n  [\"a\"] = 1,\n  [\"b\"] = \"hello\"\n}");
  }

  // /test net.wizardsoflua.tests.GlobalsTest test_sleep
  @Test
  public void test_sleep() throws Exception {
    // Given:
    long sleepTime = 10;
    // When:
    mc().executeCommand(
        "/lua print(Time.gametime); sleep(%s); print(Time.gametime)", sleepTime);

    // Then:
    ServerLog4jEvent message1 = mc().waitFor(ServerLog4jEvent.class);
    ServerLog4jEvent message2 = mc().waitFor(ServerLog4jEvent.class);
    long actual = Long.parseLong(message2.getMessage()) - Long.parseLong(message1.getMessage());
    assertThat(actual).isEqualTo(sleepTime);
  }

}
