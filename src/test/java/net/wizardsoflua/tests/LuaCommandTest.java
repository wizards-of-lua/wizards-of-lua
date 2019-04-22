package net.wizardsoflua.tests;

import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

public class LuaCommandTest extends WolTestBase {

  private static final String SHARED_PROFILE = "shared-profile";

  @AfterEach
  public void after() throws IOException {
    mc().deleteSharedModule(SHARED_PROFILE);
  }

  // /test net.wizardsoflua.tests.LuaCommandTest test_player_can_print_some_text
  @Test
  public void test_player_can_print_some_text() throws Exception {
    // Given:
    String text = "some text";

    // When:
    mc().player().chat("/lua print('%s')", text);

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
    mc().player().chat("/lua print(%s)", text);

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("91");
  }

  // /test net.wizardsoflua.tests.LuaCommandTest test_player_can_use_for_loop
  @Test
  public void test_player_can_use_for_loop() throws Exception {
    // Given:

    // When:
    mc().player().chat("/lua for i=1,10 do print(i); end");

    // Then:
    for (int i = 0; i < 10; ++i) {
      TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
      assertThat(act.getMessage()).isEqualTo(String.valueOf(i + 1));
    }
  }

  // /test net.wizardsoflua.tests.LuaCommandTest test_server_can_print_some_text
  @Test
  public void test_server_can_print_some_text() throws Exception {
    // Given:
    String text = "hello server!";

    // When:
    mc().executeCommand("/lua print('%s')", text);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(text);
  }

  // /test net.wizardsoflua.tests.LuaCommandTest test_cast_spell_with_shared_profile
  @Test
  public void test_cast_spell_with_shared_profile() throws Exception {
    // Given:
    mc().createSharedModule(SHARED_PROFILE, "function shareddummy() print('world!') end");

    // When:
    mc().executeCommand("/lua shareddummy();");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("world!");
  }

}
