package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

public class ItemsTest extends WolTestBase {

  // /test net.wizardsoflua.tests.ItemsTest test_get_diamond_axe
  @Test
  public void test_get_diamond_axe() throws Exception {
    // Given:
    String expected = "Diamond Axe";

    // When:
    mc().player().chat("/lua i=Items.get('diamond_axe'); print(i.displayName)");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

}
