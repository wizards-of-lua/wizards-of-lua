package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;
import net.wizardsoflua.testenv.net.ChatAction;

@RunWith(MinecraftJUnitRunner.class)
public class PlayerTest extends WolTestBase {

  // /test net.wizardsoflua.tests.PlayerTest test_putNbt_is_not_supported
  @Test
  public void test_putNbt_is_not_supported() throws Exception {
    // Given:

    // When:
    mc().player().perform(new ChatAction("/lua spell.owner:putNbt({})"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).contains("Error").contains("not supported");
  }

}
