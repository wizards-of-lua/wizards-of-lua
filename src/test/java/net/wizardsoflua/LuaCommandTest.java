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
    server().executeCommand(player(), "/lua print(%s)", text);

    // Then:
    Iterable<String> act = server().getChatOutputOf(player());
    assertThat(act).containsOnly(text);
  }

  @Test
  public void test_print_some_calculation() throws Exception {
    // Given:
    String text = "13 * 7";

    // When:
    server().executeCommand(player(), "/lua print(%s)", text);

    // Then:
    Iterable<String> act = server().getChatOutputOf(player());
    assertThat(act).containsOnly("101");
  }
}
