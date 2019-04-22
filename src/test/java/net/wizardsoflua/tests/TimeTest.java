package net.wizardsoflua.tests;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

public class TimeTest extends WolTestBase {

  // /test net.wizardsoflua.tests.TimeTest test_getDate_executed_by_player
  @Test
  public void test_getDate_executed_by_player() throws Exception {
    // Given:
    LocalDateTime now = LocalDateTime.now();
    String expected = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    mc().freezeClock(now);

    // When:
    mc().player().chat("/lua print(Time.getDate())");

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
    mc().executeCommand("/lua \n"//
        + "print(Time.gametime)\n"//
        + "Time.sleep(" + sleepTime + ")\n"//
        + "print(Time.gametime)\n"//
    );

    // Then:
    String message1 = mc().nextServerMessage();
    String message2 = mc().nextServerMessage();
    long actual = Long.parseLong(message2) - Long.parseLong(message1);
    assertThat(actual).isEqualTo(sleepTime);
  }

  // /test net.wizardsoflua.tests.TimeTest test_sleep__With_zero
  @Test
  public void test_sleep__With_zero() throws Exception {
    // Given:
    long sleepTime = 0;

    // When:
    mc().executeCommand("/lua \n"//
        + "print(Time.gametime)\n"//
        + "Time.sleep(" + sleepTime + ")\n"//
        + "print(Time.gametime)\n"//
    );

    // Then:
    String message1 = mc().nextServerMessage();
    String message2 = mc().nextServerMessage();
    long actual = Long.parseLong(message2) - Long.parseLong(message1);
    assertThat(actual).isEqualTo(sleepTime);
  }

  // /test net.wizardsoflua.tests.TimeTest test_sleep__With_negative_arg
  @Test
  public void test_sleep__With_negative_arg() throws Exception {
    // Given:
    long sleepTime = -1;

    // When:
    mc().executeCommand("/lua Time.sleep(%s)", sleepTime);

    // Then:
    assertThat(mc().nextServerMessage()).contains("attempt to sleep a negative amount of ticks");
  }

  // /test net.wizardsoflua.tests.TimeTest test_sleep__With_nil__Allowance_is_above_sleep_trigger
  @Test
  public void test_sleep__With_nil__Allowance_is_above_sleep_trigger() throws Exception {
    // When:
    mc().executeCommand("/lua \n"//
        + "print(Time.gametime)\n"//
        + "Time.sleep()\n"//
        + "print(Time.gametime)\n"//
    );

    // Then:
    String message1 = mc().nextServerMessage();
    String message2 = mc().nextServerMessage();
    long actual = Long.parseLong(message2) - Long.parseLong(message1);
    assertThat(actual).isZero();
  }

  // /test net.wizardsoflua.tests.TimeTest test_sleep__With_nil__Allowance_is_below_sleep_trigger
  @Test
  public void test_sleep__With_nil__Allowance_is_below_sleep_trigger() throws Exception {
    // Given:
    int repetitions = 400;
    mc().setLuaTicksLimit(2 * repetitions * 3); // 3 ticks per cycle

    // When:
    mc().executeCommand("/lua \n"//
        + "for consumeTicks=1," + repetitions + " do end\n"//
        + "print(Time.gametime)\n"//
        + "Time.sleep()\n"//
        + "print(Time.gametime)\n"//
    );

    // Then:
    String message1 = mc().nextServerMessage();
    String message2 = mc().nextServerMessage();
    long actual = Long.parseLong(message2) - Long.parseLong(message1);
    assertThat(actual).isEqualTo(1);
  }

  // /test net.wizardsoflua.tests.TimeTest test_spell_will_be_terminated_when_autosleep_is_off
  @Test
  public void test_spell_will_be_terminated_when_autosleep_is_off() throws Exception {
    // Given:
    int repetitions = 2000;
    mc().setLuaTicksLimit(5 * repetitions); // 5 ticks per cycle

    // When:
    mc().player().chat("/lua Time.autosleep=false; sleep(1); for i=1,%s do print(i); end",
        repetitions);

    // Then:
    for (int i = 1; i < repetitions; ++i) {
      TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
      assertThat(act.getMessage()).isEqualTo(String.valueOf(i));
    }
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).contains("Spell has been terminated automatically");
  }

  // @formatter:off
  // /test net.wizardsoflua.tests.TimeTest
  // test_spell_will_be_terminated_when_event_listener_takes_to_long
  // @formatter:on
  @Test
  public void test_spell_will_be_terminated_when_event_listener_takes_to_long() throws Exception {
    // Given:
    int repetitions = 1000;
    mc().setEventListenerLuaTicksLimit(5 * repetitions); // 5 ticks per cycle

    // When:
    mc().player().chat(
        "/lua Events.on('custom-event'):call(function(event) for i=1,%s do print(i); end; end); Events.fire('custom-event')",
        repetitions);

    // Then:
    for (int i = 1; i < repetitions; ++i) {
      TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
      assertThat(act.getMessage()).isEqualTo(String.valueOf(i));
    }
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).contains("Spell has been terminated automatically");
  }

}
