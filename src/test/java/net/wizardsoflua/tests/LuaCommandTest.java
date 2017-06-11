package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.client.ChatAction;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

@RunWith(MinecraftJUnitRunner.class)
public class LuaCommandTest extends WolTestBase {

  // /test net.wizardsoflua.tests.LuaCommandTest test_player_can_print_some_text
  @Test
  public void test_player_can_print_some_text() throws Exception {
    // Given:
    String text = "some text";

    // When:
    mc().player().perform(new ChatAction("/lua print('%s')", text));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(text);
  }

  // /test net.wizardsoflua.tests.LuaCommandTest test_player_can_print_some_calculation
  @Test
  public void test_player_can_print_some_calculation() throws Exception {
    // Given:
    String text = "13 * 7";

    // When:
    mc().player().perform(new ChatAction("/lua print(%s)", text));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("91");
  }
  
 // /test net.wizardsoflua.tests.LuaCommandTest test_server_can_print_some_calculation
// @Test
// public void test_server_can_print_some_calculation() throws Exception {
//   // Given:
//   String text = "13 * 7";
//
//   // When:
//   mc().executeCommand("/lua print(%s)", text);
//
//   // Then:
//   TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
//   assertThat(act.getMessage()).isEqualTo("91");
// }
  
  
}
