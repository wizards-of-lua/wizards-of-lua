package net.wizardsoflua;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;

@RunWith(MinecraftJUnitRunner.class)
public class LuaCommandTest extends WolTestBase {

  @Test
  public void test_print_some_text() throws Exception {
    // Given:
    String text = "some text";

    // When:
    mc().executeCommand(mc().player(), "/lua print(%s)", text);

    // Then:
    Iterable<String> act = mc().getChatOutputOf(mc().player());
    assertThat(act).containsOnly(text);
  }

  @Test
  public void test_print_some_calculation() throws Exception {
    // Given:
    String text = "13 * 7";

    // When:
    mc().executeCommand(mc().player(), "/lua print(%s)", text);

    // Then:
    Iterable<String> act = mc().getChatOutputOf(mc().player());
    assertThat(act).containsOnly("101");
  }
}
