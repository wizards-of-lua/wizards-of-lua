package net.wizardsoflua;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraftforge.event.ServerChatEvent;
import net.wizardsoflua.testenv.InGameTestBase;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;

@RunWith(MinecraftJUnitRunner.class)
public class GameTest extends InGameTestBase {

  @Test
  public void test_1() {
    Assertions.assertThat(server().getName()).isEqualTo("Server");
  }

  @Test
  public void test_2() {
    // Given:
    String message = "hello";

    // When:
    server().post(newServerChatEvent(player(), message));

    // Then:
    Iterable<ServerChatEvent> act = server().chatEvents();
    assertThat(messagesOf(act)).containsExactly(message);
  }
  
  @Test
  public void test_3() {
    // Given:
    String message = "hui";
    String expectedMessage = "huiiii"; // let's see if we get an error message

    // When:
    server().post(newServerChatEvent(player(), message));

    // Then:
    Iterable<ServerChatEvent> act = server().chatEvents();
    assertThat(messagesOf(act)).containsExactly(expectedMessage);
  }

}
