package net.wizardsoflua.tests;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;
import net.wizardsoflua.testenv.net.ChatAction;

@RunWith(MinecraftJUnitRunner.class)
public class PlayerTest extends WolTestBase {

  private static final String DEMOMODULE = "my.demomodule";

  @After
  public void after() {
    mc().deleteTeams();
    mc().deletePlayerModule(DEMOMODULE);
  }

  // /test net.wizardsoflua.tests.PlayerTest test_putNbt_is_not_supported
  @Test
  public void test_putNbt_is_not_supported() throws Exception {
    // Given:

    // When:
    mc().player().perform(new ChatAction("/lua spell.owner:putNbt({})"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).contains("Error").contains("not supported");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_team_is_readable
  @Test
  public void test_team_is_readable() throws Exception {
    // Given:
    String team = "demo1";
    mc().createTeam(team);
    mc().player().setTeam(team);

    // When:
    mc().player().perform(new ChatAction("/lua p=spell.owner; print(p.team)"));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(team);
  }

  // /test net.wizardsoflua.tests.PlayerTest test_team_is_writable
  @Test
  public void test_team_is_writable() throws Exception {
    // Given:
    String team = "demo2";
    mc().createTeam(team);

    // When:
    mc().player().perform(new ChatAction("/lua p=spell.owner; p.team='%s'; print('ok')", team));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
    assertThat(mc().player().getTeam()).isEqualTo(team);
  }

  // /test net.wizardsoflua.tests.PlayerTest test_require_player_module
  @Test
  public void test_require_player_module() throws Exception {
    // Given:
    mc().createPlayerModule(DEMOMODULE, "function dummy() print('hello') end");

    // When:
    mc().player().perform(new ChatAction("/lua require('%s'); dummy();", DEMOMODULE));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("hello");
  }

}
