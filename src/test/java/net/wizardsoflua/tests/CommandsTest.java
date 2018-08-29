package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class CommandsTest extends WolTestBase {

  // /test net.wizardsoflua.tests.CommandsTest test_register_command
  @Test
  public void test_register_command() throws Exception {
    // Given:

    mc().executeCommand("/lua Commands.register('dummy',[[ print('hello') ]])");
    mc().clearEvents();

    // When:
    mc().executeCommand("/dummy");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("hello");
  }

  // /test net.wizardsoflua.tests.CommandsTest test_deregister_command
  @Test
  public void test_deregister_command() throws Exception {
    // Given:

    mc().executeCommand("/lua Commands.register('dummy',[[ print('hello') ]])");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua Commands.deregister('dummy')");
    mc().executeCommand("/dummy");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).contains("Unknown command");
  }

  // /test net.wizardsoflua.tests.CommandsTest test_register_command_with_arguments
  @Test
  public void test_register_command_with_arguments() throws Exception {
    // Given:

    mc().executeCommand("/lua Commands.register('dummy',[[ a=select(1,...); b=select(2,...); print(a,b);]])");
    mc().clearEvents();

    // When:
    mc().executeCommand("/dummy x y");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("x   y");
  }

}
