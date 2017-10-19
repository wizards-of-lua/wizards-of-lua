package net.wizardsoflua.tests;

import java.io.IOException;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

/**
 * Testing the "/wol autoRequire" command
 */
@RunWith(MinecraftJUnitRunner.class)
public class WolAutoRequireTest extends WolTestBase {

  @After
  public void clearUserConfigs() throws IOException {
    mc().clearWizardConfigs();
  }

  // /test net.wizardsoflua.tests.WolAutoRequireTest test_AutoRequire_not_set
  @Test
  public void test_AutoRequire_not_set() throws Exception {
    // Given:
    String expected = "[WoL] autoRequire is not set";

    // When:
    mc().player().chat("/wol autoRequire");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WolAutoRequireTest test_AutoRequire_not_set
  @Test
  public void test_autoRequire_set() throws Exception {
    // Given:
    String module = "mymodule";
    String expected = String.format("[WoL] autoRequire = \"%s\"", module);
    mc().player().setAutoRequire(module);

    // When:
    mc().player().chat("/wol autoRequire");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WolAutoRequireTest test_AutoRequire_set
  @Test
  public void test_AutoRequire_set() throws Exception {
    // Given:
    String module = "mymodule";
    String expected = String.format("[WoL] autoRequire = \"%s\"", module);

    // When:
    mc().player().chat("/wol autoRequire set %s", module);

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WolAutoRequireTest test_autoRequire_unset
  @Test
  public void test_autoRequire_unset() throws Exception {
    // Given:
    String module = "mymodule";
    String expected = "[WoL] unset autoRequire";
    mc().player().setAutoRequire(module);

    // When:
    mc().player().chat("/wol autoRequire unset");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
    String actModule = mc().player().getAutoRequire();
    assertThat(actModule).isNull();
  }

}
