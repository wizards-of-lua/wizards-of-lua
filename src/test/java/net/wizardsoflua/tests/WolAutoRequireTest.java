package net.wizardsoflua.tests;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;
import net.wizardsoflua.testenv.net.ChatAction;

/**
 * Testing the "/wol autoRequire" command
 */
@RunWith(MinecraftJUnitRunner.class)
public class WolAutoRequireTest extends WolTestBase {

  @After
  public void clearUserConfigs() {
    mc().clearWizardConfigs();
  }

  // /test net.wizardsoflua.tests.WolAutoRequireTest test_profile_returns_profile_is_not_set
  @Test
  public void test_profile_returns_profile_is_not_set() throws Exception {
    // Given:
    String expected = "[WoL] autoRequire is not set";

    // When:
    mc().player().perform(new ChatAction("/wol autoRequire"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WolAutoRequireTest test_profile_returns_current_profile
  @Test
  public void test_profile_returns_current_profile() throws Exception {
    // Given:
    String module = "mymodule";
    String expected = String.format("[WoL] autoRequire = \"%s\"", module);
    mc().player().setProfile(module);

    // When:
    mc().player().perform(new ChatAction("/wol autoRequire"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WolAutoRequireTest test_profile_set
  @Test
  public void test_profile_set() throws Exception {
    // Given:
    String module = "mymodule";
    String expected = String.format("[WoL] autoRequire = \"%s\"", module);

    // When:
    mc().player().perform(new ChatAction("/wol autoRequire set %s", module));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WolAutoRequireTest test_profile_unset
  @Test
  public void test_profile_unset() throws Exception {
    // Given:
    String module = "mymodule";
    String expected = "[WoL] unset autoRequire";
    mc().player().setProfile(module);

    // When:
    mc().player().perform(new ChatAction("/wol autoRequire unset"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
    String actModule = mc().player().getPlayerProfile();
    assertThat(actModule).isNull();
  }

}
