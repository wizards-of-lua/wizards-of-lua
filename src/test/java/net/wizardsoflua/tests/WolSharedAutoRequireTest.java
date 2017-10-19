package net.wizardsoflua.tests;

import java.io.IOException;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

/**
 * Testing the "/wol sharedAutoRequire" command
 */
@RunWith(MinecraftJUnitRunner.class)
public class WolSharedAutoRequireTest extends WolTestBase {

  @After
  public void clearUserConfigs() throws IOException {
    mc().clearSharedAutoRequire();
  }

  // /test net.wizardsoflua.tests.WolSharedAutoRequireTest test_sharedAutoRequire_not_set
  @Test
  public void test_sharedAutoRequire_not_set() throws Exception {
    // Given:
    String expected = "[WoL] sharedAutoRequire is not set";

    // When:
    mc().player().chat("/wol sharedAutoRequire");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WolSharedAutoRequireTest test_sharedAutoRequire_is_set
  @Test
  public void test_sharedAutoRequire_is_set() throws Exception {
    // Given:
    String module = "mymodule";
    String expected = String.format("[WoL] sharedAutoRequire = \"%s\"", module);
    mc().setSharedAutoRequire(module);

    // When:
    mc().player().chat("/wol sharedAutoRequire");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WolSharedAutoRequireTest test_sharedAutoRequire_set
  @Test
  public void test_sharedAutoRequire_set() throws Exception {
    // Given:
    String module = "mymodule";
    String expected = String.format("[WoL] sharedAutoRequire = \"%s\"", module);

    // When:
    mc().player().chat("/wol sharedAutoRequire set %s", module);

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WolSharedAutoRequireTest test_sharedAutoRequire_unset
  @Test
  public void test_sharedAutoRequire_unset() throws Exception {
    // Given:
    String module = "mymodule";
    String expected = "[WoL] unset sharedAutoRequire";
    mc().setSharedAutoRequire(module);

    // When:
    mc().player().chat("/wol sharedAutoRequire unset");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
    String actModule = mc().getSharedAutoRequire();
    assertThat(actModule).isNull();
  }

}
