package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

/**
 * Testing the "/wol pack export" command
 */
public class WolPackExportTest extends WolTestBase {

  // /test net.wizardsoflua.tests.WolPackExportTest test_pack_export__Executed_by_server
  @Test
  public void test_pack_export__Executed_by_server() throws Exception {
    // Given:
    mc().createSharedModule("dummy-module.dummy", "-- some dummy module");

    // When:
    mc().executeCommand("/wol pack export dummy-module");

    // Then:
    String actual = mc().nextServerMessage();
    assertThat(actual).startsWith("[WoL] Click here to download:");
    assertThat(actual).contains("dummy-module.jar");
  }

  // /test net.wizardsoflua.tests.WolPackExportTest test_pack_export__Executed_by_player
  @Test
  public void test_pack_export__Executed_by_player() throws Exception {
    // Given:
    mc().createSharedModule("dummy-module.dummy", "-- some dummy module");

    // When:
    mc().player().chat("/wol pack export dummy-module");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).startsWith("[WoL] Click here to download:");
    assertThat(act.getMessage()).contains("dummy-module.jar");
  }
}
