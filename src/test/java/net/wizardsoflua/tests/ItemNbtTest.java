package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class ItemNbtTest extends WolTestBase {

  // /test net.wizardsoflua.tests.ItemNbtTest test_String_NBT_is_readable
  @Test
  public void test_String_NBT_is_readable() throws Exception {
    // Given:
    String expected = "bla";

    // When:
    mc().executeCommand("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = { title = '" + expected + "'}\n"//
        + "print(item.nbt.tag.title)\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_String_NBT_is_writable
  @Test
  public void test_String_NBT_is_writable() throws Exception {
    // Given:
    String expected = "bla";

    // When:
    mc().executeCommand("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = { title = 'bli' }\n"//
        + "item.nbt.tag.title = '" + expected + "'\n"//
        + "print(item.nbt.tag.title)\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

}
