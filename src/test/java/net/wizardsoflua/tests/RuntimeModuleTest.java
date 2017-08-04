package net.wizardsoflua.tests;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;
import net.wizardsoflua.testenv.net.ChatAction;

@RunWith(MinecraftJUnitRunner.class)
public class RuntimeModuleTest extends WolTestBase {

  // /test net.wizardsoflua.tests.RuntimeModuleTest test_getRealDateTime_executed_by_player
  @Test
  public void test_getRealDateTime_executed_by_player() throws Exception {
    // Given:
    LocalDateTime now = LocalDateTime.now();
    String expected = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    mc().freezeClock(now);

    // When:
    mc().player().perform(new ChatAction("/lua print(Runtime.getRealDateTime())"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.RuntimeModuleTest test_getRealDateTime_executed_by_server
  @Test
  public void test_getRealDateTime_executed_by_server() throws Exception {
    // Given:
    LocalDateTime now = LocalDateTime.now();
    String expected = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    mc().freezeClock(now);

    // When:
    mc().executeCommand("/lua print(Runtime.getRealDateTime())");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  /// test net.wizardsoflua.tests.RuntimeModuleTest test_getRealtime_executed_by_player
  @Test
  public void test_getRealtime_executed_by_player() throws Exception {
    // Given:
    long now = System.currentTimeMillis();
    String expected = String.valueOf(now);
    mc().freezeClock(now);

    // When:
    mc().player().perform(new ChatAction("/lua print(Runtime.getRealtime())"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.RuntimeModuleTest test_getRealtime_executed_by_server
  @Test
  public void test_getRealtime_executed_by_server() throws Exception {
    // Given:
    long now = System.currentTimeMillis();
    String expected = String.valueOf(now);
    mc().freezeClock(now);

    // When:
    mc().executeCommand("/lua print(Runtime.getRealtime())");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

}
