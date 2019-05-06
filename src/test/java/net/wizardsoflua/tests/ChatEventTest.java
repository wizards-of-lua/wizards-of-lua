package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.wizardsoflua.testenv.WolTestBase;

public class ChatEventTest extends WolTestBase {

  // /test net.wizardsoflua.tests.ChatEventTest test
  @Test
  public void test() {
    // Given:
    String message = "hello world!";
    String expected1 = String.format("<%s> %s", mc().player().getName(), message);
    String expected2 = message;

    mc().executeCommand("/lua q=Events.collect('ChatEvent'); e=q:next(); print(e.message)");

    // When:
    mc().player().chat(message);

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo(expected1);
    assertThat(mc().nextServerMessage()).isEqualTo(expected2);
  }

}
