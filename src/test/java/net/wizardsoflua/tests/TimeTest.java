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
public class TimeTest extends WolTestBase {

  // /test net.wizardsoflua.tests.TimeTest test_getDate_executed_by_player
  @Test
  public void test_getDate_executed_by_player() throws Exception {
    // Given:
    LocalDateTime now = LocalDateTime.now();
    String expected = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    mc().freezeClock(now);

    // When:
    mc().player().perform(new ChatAction("/lua print(Time.getDate())"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.TimeTest test_getDate_executed_by_server
  @Test
  public void test_getDate_executed_by_server() throws Exception {
    // Given:
    LocalDateTime now = LocalDateTime.now();
    String expected = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    mc().freezeClock(now);

    // When:
    mc().executeCommand("/lua print(Time.getDate())");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.TimeTest test_realtime
  @Test
  public void test_realtime() throws Exception {
    // Given:
    long now = System.currentTimeMillis();
    String expected = String.valueOf(now);
    mc().freezeClock(now);

    // When:
    mc().executeCommand("/lua print(Time.realtime)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.TimeTest test_sleep
  @Test
  public void test_sleep() throws Exception {
    // Given:
    long sleepTime = 10;
    // When:
    mc().executeCommand("/lua print(Time.gametime); Time.sleep(%s); print(Time.gametime)",
        sleepTime);

    // Then:
    ServerLog4jEvent message1 = mc().waitFor(ServerLog4jEvent.class);
    ServerLog4jEvent message2 = mc().waitFor(ServerLog4jEvent.class);
    long actual = Long.parseLong(message2.getMessage()) - Long.parseLong(message1.getMessage());
    assertThat(actual).isEqualTo(sleepTime);
  }

  // /test net.wizardsoflua.tests.TimeTest test_spell_will_be_broken_when_autosleep_is_off
  @Test
  public void test_spell_will_be_broken_when_autosleep_is_off() throws Exception {
    // Given:
    int repetitions = 2000;
    mc().setLuaTicksLimit(10000);
    // When:
    mc().player().perform(
        new ChatAction("/lua Time.autosleep=false; for i=1,%s do print(i); end", repetitions));

    // Then:
    for (int i = 1; i < 2000; ++i) {
      TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
      assertThat(act.getMessage()).isEqualTo(String.valueOf(i));
    }
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).contains("Spell has been broken automatically");
  }

}
