package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.wizardsoflua.testenv.WolTestBase;

public class LoggersTest extends WolTestBase {

  // /test net.wizardsoflua.tests.LoggersTest test_can_write_info_message_to_minecraft_logger
  @Test
  public void test_can_write_info_message_to_minecraft_logger() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua l=Loggers.get('net.minecraft'); l:info('hello')");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("hello");
  }

  // /test net.wizardsoflua.tests.LoggersTest
  // test_can_write_formatted_info_message_to_minecraft_logger
  @Test
  public void test_can_write_formatted_info_message_to_minecraft_logger() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua l=Loggers.get('net.minecraft'); l:info('hello %s %s',1,2)");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("hello 1 2");
  }

  // /test net.wizardsoflua.tests.LoggersTest test_can_write_error_message_to_minecraft_logger
  @Test
  public void test_can_write_error_message_to_minecraft_logger() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua l=Loggers.get('net.minecraft'); l:error('hello')");

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("hello");
  }

}
