package net.wizardsoflua.tests;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;
import net.wizardsoflua.testenv.net.ChatAction;

/**
 * Testing the "/wol require" command
 */
@RunWith(MinecraftJUnitRunner.class)
public class WolRequireTest extends WolTestBase {

  @After
  public void clearUserConfigs() {
    mc().clearUserConfigs();
  }

  // /test net.wizardsoflua.tests.WolRequireTest test_profile_returns_profile_is_not_set
  @Test
  public void test_profile_returns_profile_is_not_set() throws Exception {
    // Given:
    String expected = "[WoL] required module is not set";

    // When:
    mc().player().perform(new ChatAction("/wol require"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WolRequireTest test_profile_returns_current_profile
  @Test
  public void test_profile_returns_current_profile() throws Exception {
    // Given:
    String module = "mymodule";
    String expected = String.format("[WoL] require = \"%s\"", module);
    mc().player().setProfile(module);

    // When:
    mc().player().perform(new ChatAction("/wol require"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WolRequireTest test_profile_set
  @Test
  public void test_profile_set() throws Exception {
    // Given:
    String module = "mymodule";
    String expected = String.format("[WoL] require = \"%s\"", module);

    // When:
    mc().player().perform(new ChatAction("/wol require set %s", module));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WolRequireTest test_profile_unset
  @Test
  public void test_profile_unset() throws Exception {
    // Given:
    String module = "mymodule";
    String expected = "[WoL] unset required module";
    mc().player().setProfile(module);

    // When:
    mc().player().perform(new ChatAction("/wol require unset"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
    String actModule = mc().player().getPlayerProfile();
    assertThat(actModule).isNull();
  }

}
