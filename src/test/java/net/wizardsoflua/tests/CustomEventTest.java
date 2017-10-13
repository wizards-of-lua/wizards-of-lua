package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class CustomEventTest extends WolTestBase {

  // /test net.wizardsoflua.tests.CustomEventTest test
  @Test
  public void test() {
    // Given:
    String eventName = "my-custom-event-name";
    String message = "hello world!";
    String expected = "received " + message;

    mc().executeCommand("/lua q=Events.register('%s'); e=q:pop(); print('received '..e.data)",
        eventName);

    // When:
    mc().executeCommand("/lua Events.fire('%s','%s')", eventName, message);

    // Then:
    ServerLog4jEvent act1 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act1.getMessage()).isEqualTo(expected);
  }

}
