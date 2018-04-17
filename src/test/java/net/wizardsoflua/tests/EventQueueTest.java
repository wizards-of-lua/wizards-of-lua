package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class EventQueueTest extends WolTestBase {

  // /test net.wizardsoflua.tests.EventQueueTest test_latest__Two_custom_Events
  @Test
  public void test_latest__Two_custom_Events() {
    // When:
    mc().executeCommand("lua q=Events.collect('custom-event')\n"//
        + "Events.collect('continue'):next()\n"//
        + "e=q:latest()\n"//
        + "print(e.data)\n"//
        + "print(str(q:isEmpty()))\n"//
    );
    mc().executeCommand("lua Events.fire('custom-event', 1)");
    mc().executeCommand("lua Events.fire('custom-event', 2)");
    mc().executeCommand("lua Events.fire('continue')");

    // Then:
    ServerLog4jEvent act1 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act1.getMessage()).isEqualTo("2");
    ServerLog4jEvent act2 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act2.getMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.EventQueueTest test_latest__No_event
  @Test
  public void test_latest__No_event() {
    // When:
    mc().executeCommand(
        "/lua q=Events.collect('LeftClickBlockEvent'); sleep(20); e=q:latest(); print(str(e == nil))");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }

}
