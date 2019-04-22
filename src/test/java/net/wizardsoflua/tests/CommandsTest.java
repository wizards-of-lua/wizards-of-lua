package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

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

    mc().executeCommand(
        "/lua Commands.register('dummy',[[ a=select(1,...); b=select(2,...); print(a,b);]])");
    mc().clearEvents();

    // When:
    mc().executeCommand("/dummy x y");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("x   y");
  }

  // /test net.wizardsoflua.tests.CommandsTest test_use_command_with_permission_level_as_operator
  @Test
  public void test_use_command_with_permission_level_as_operator() throws Exception {
    // Given:
    mc().player().setOperator(true);
    mc().executeCommand("/lua Commands.register('dummy',[[ print('hello') ]],'/dummy', 1)");
    mc().clearEvents();

    // When:
    mc().player().chat("/dummy");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("hello");
  }

  // /test net.wizardsoflua.tests.CommandsTest
  // test_use_command_with_permission_level_not_as_operator
  @Test
  public void test_use_command_with_permission_level_not_as_operator() throws Exception {
    // Given:
    mc().player().setOperator(false);
    mc().executeCommand("/lua Commands.register('dummy',[[ print('hello') ]],'/dummy', 1)");
    mc().clearEvents();

    // When:
    mc().player().chat("/dummy");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("You do not have permission to use this command");
  }

}
