package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class ChatEventTest extends WolTestBase {

  // /test net.wizardsoflua.tests.ChatEventTest test
  @Test
  public void test() {
    // Given:
    String message = "hello world!";
    String expected1 = String.format("<%s> %s", mc().player().getName(), message);
    String expected2 = message;

    mc().executeCommand("/lua q=Events.register('ChatEvent'); e=q:pop(); print(e.message)");

    // When:
    mc().player().chat(message);

    // Then:
    ServerLog4jEvent act1 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act1.getMessage()).isEqualTo(expected1);
    ServerLog4jEvent act2 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act2.getMessage()).isEqualTo(expected2);
  }

}
