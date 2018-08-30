package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class LoggersTest extends WolTestBase {

  // /test net.wizardsoflua.tests.LoggersTest test_can_write_info_message_to_minecraft_logger
  @Test
  public void test_can_write_info_message_to_minecraft_logger() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua l=Loggers.get('net.minecraft'); l:info('hello')");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("hello");
  }

  // /test net.wizardsoflua.tests.LoggersTest
  // test_can_write_formatted_info_message_to_minecraft_logger
  @Test
  public void test_can_write_formatted_info_message_to_minecraft_logger() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua l=Loggers.get('net.minecraft'); l:info('hello %s %s',1,2)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("hello 1 2");
  }

  // /test net.wizardsoflua.tests.LoggersTest test_can_write_error_message_to_minecraft_logger
  @Test
  public void test_can_write_error_message_to_minecraft_logger() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua l=Loggers.get('net.minecraft'); l:error('hello')");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("hello");
  }

}
