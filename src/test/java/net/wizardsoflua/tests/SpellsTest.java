package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

public class SpellsTest extends WolTestBase {

  // /test net.wizardsoflua.tests.SpellsTest test_find_self_without_criteria
  @Test
  public void test_find_self_without_criteria() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua spells=Spells.find(); print(#spells)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("1");
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_several_spells_without_criteria_1
  @Test
  public void test_find_several_spells_without_criteria_1() throws Exception {
    // Given:
    int count = 10;
    for (int i = 0; i < count; ++i) {
      mc().executeCommand("/lua sleep(20*10)");
    }
    String expected = String.valueOf(count + 1);

    // When:
    mc().executeCommand("/lua spells=Spells.find({}); print(#spells)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_several_spells_without_criteria_2
  @Test
  public void test_find_several_spells_without_criteria_2() throws Exception {
    // Given:
    int count = 10;
    for (int i = 0; i < count; ++i) {
      mc().executeCommand("/lua sleep(20*10)");
    }
    String expected = String.valueOf(count + 1);

    // When:
    mc().executeCommand("/lua spells=Spells.find(); print(#spells)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_exclude_self
  @Test
  public void test_find_exclude_self() throws Exception {
    // Given:
    String name = "other";
    mc().executeCommand("/lua spell.name='%s'; sleep(20*10)", name);
    String expected = "1   other";

    // When:
    mc().executeCommand(
        "/lua spells=Spells.find({excludeSelf=true}); print(#spells, spells[1].name)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_by_name
  @Test
  public void test_find_by_name() throws Exception {
    // Given:
    String name = "other";
    mc().executeCommand("/lua spell.name='%s'; sleep(20*10)", name);
    String expected = "1   other";

    // When:
    mc().executeCommand("/lua spells=Spells.find({name='%s'}); print(#spells, spells[1].name)",
        name);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_several_spells_by_name
  @Test
  public void test_find_several_spells_by_name() throws Exception {
    // Given:
    String name = "some-name";
    int count = 10;
    for (int i = 0; i < count; ++i) {
      mc().executeCommand("/lua spell.name='%s'; sleep(20*10)", name);
    }
    String expected = String.valueOf(count);

    // When:
    mc().executeCommand("/lua spells=Spells.find({name='%s'}); print(#spells)", name);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_by_tag
  @Test
  public void test_find_by_tag() throws Exception {
    // Given:
    String name = "other";
    String tag = "some-tag";
    mc().executeCommand("/lua spell.name='%s'; spell:addTag('%s'); sleep(20*10)", name, tag);
    String expected = "1   other";

    // When:
    mc().executeCommand("/lua spells=Spells.find({tag='%s'}); print(#spells, spells[1].name)", tag);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_several_spells_by_tag
  @Test
  public void test_find_several_spells_by_tag() throws Exception {
    // Given:
    String tag = "some-tag";
    int count = 10;
    for (int i = 0; i < count; ++i) {
      mc().executeCommand("/lua spell:addTag('%s'); sleep(20*10)", tag);
    }
    String expected = String.valueOf(count);
    String otherTag = "some-other-tag";
    mc().executeCommand("/lua spell:addTag('%s'); sleep(20*10)", otherTag);

    // When:
    mc().executeCommand("/lua spells=Spells.find({tag='%s'}); print(#spells)", tag);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_by_tag_and_name
  @Test
  public void test_find_by_tag_and_name() throws Exception {
    // Given:
    String name = "other";
    String tag = "some-tag";
    mc().executeCommand("/lua spell.name='%s'; spell:addTag('%s'); sleep(20*10)", name, tag);
    String expected = "1   other";

    // When:
    mc().executeCommand(
        "/lua spells=Spells.find({tag='%s',name='%s'}); print(#spells, spells[1].name)", tag, name);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_by_sid
  @Test
  public void test_find_by_sid() throws Exception {
    // Given:
    String name = "other";
    mc().executeCommand("/lua spell.name='%s'; sleep(20*10)", name);
    SpellEntity other = mc().spells().iterator().next();
    String expected = "1   other";

    // When:
    mc().executeCommand("/lua spells=Spells.find({sid=%s}); print(#spells,spells[1].name)",
        other.getSid());

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_by_owner
  @Test
  public void test_find_by_owner() throws Exception {
    // Given:
    String name = "other";
    mc().player().chat("/lua spell.name='%s'; print('ok'); sleep(20*10);", name);
    assertThat(mc().waitFor(TestPlayerReceivedChatEvent.class).getMessage()).isEqualTo("ok");
    String player = mc().player().getName();
    String expected = "1   other";

    // When:
    mc().executeCommand("/lua spells=Spells.find({owner='%s'}); print(#spells,spells[1].name)",
        player, expected);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_by_max_radius_0
  @Test
  public void test_find_by_max_radius_0() throws Exception {
    // Given:
    String name = "other";
    mc().executeCommand("/lua spell.name='%s'; sleep(20*10)", name);
    String expected = "1   other";

    // When:
    mc().executeCommand(
        "/lua spells=Spells.find({maxradius=0,excludeSelf=true}); print(#spells,spells[1].name)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_by_max_radius_1_in_range
  @Test
  public void test_find_by_max_radius_1_in_range() throws Exception {
    // Given:
    String name = "other";
    mc().executeCommand("/lua spell.name='%s'; sleep(20*10)", name);
    String expected = "1   other";

    // When:
    mc().executeCommand(
        "/lua spell:move('up'); spells=Spells.find({maxradius=1,excludeSelf=true}); print(#spells,spells[1].name)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_by_max_radius_1_out_of_range
  @Test
  public void test_find_by_max_radius_1_out_of_range() throws Exception {
    // Given:
    String name = "other";
    mc().executeCommand("/lua spell.name='%s'; sleep(20*10)", name);
    String expected = "0";

    // When:
    mc().executeCommand(
        "/lua spell:move('up',2); spells=Spells.find({maxradius=1,excludeSelf=true}); print(#spells)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_by_min_radius_0
  @Test
  public void test_find_by_min_radius_0() throws Exception {
    // Given:
    String name = "other";
    mc().executeCommand("/lua spell.name='%s'; sleep(20*10)", name);
    String expected = "1   other";

    // When:
    mc().executeCommand(
        "/lua spells=Spells.find({minradius=0,excludeSelf=true}); print(#spells,spells[1].name)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_by_min_radius_1_in_range
  @Test
  public void test_find_by_min_radius_1_in_range() throws Exception {
    // Given:
    String name = "other";
    mc().executeCommand("/lua spell.name='%s'; sleep(20*10)", name);
    String expected = "1   other";

    // When:
    mc().executeCommand(
        "/lua spell:move('up'); spells=Spells.find({minradius=1,excludeSelf=true}); print(#spells,spells[1].name)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_by_min_radius_1_out_of_range
  @Test
  public void test_find_by_min_radius_1_out_of_range() throws Exception {
    // Given:
    String name = "other";
    mc().executeCommand("/lua spell.name='%s'; sleep(20*10)", name);
    String expected = "0";

    // When:
    mc().executeCommand("/lua spells=Spells.find({minradius=1,excludeSelf=true}); print(#spells)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_by_min_and_max_radius_1_in_range
  @Test
  public void test_find_by_min_and_max_radius_1_in_range() throws Exception {
    // Given:
    String name = "other";
    mc().executeCommand("/lua spell.name='%s'; sleep(20*10)", name);
    String expected = "1   other";

    // When:
    mc().executeCommand(
        "/lua spell:move('up'); spells=Spells.find({minradius=1, maxradius=1, excludeSelf=true}); print(#spells,spells[1].name)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.SpellsTest test_find_by_min_and_max_radius_in_range
  @Test
  public void test_find_by_min_and_max_radius_in_range() throws Exception {
    // Given:
    String name = "other";
    mc().executeCommand("/lua spell.name='%s'; sleep(20*10)", name);
    String expected = "1   other";

    // When:
    mc().executeCommand(
        "/lua spell:move('up',1.5); spells=Spells.find({minradius=1, maxradius=2, excludeSelf=true}); print(#spells,spells[1].name)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

}
