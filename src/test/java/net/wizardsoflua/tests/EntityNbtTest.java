package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import net.minecraft.util.math.Vec3d;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class EntityNbtTest extends WolTestBase {

  // /test net.wizardsoflua.tests.EntityNbtTest test_Double_NBT_is_readable
  @Test
  public void test_Double_NBT_is_readable() throws Exception {
    // Given:
    String expected = "5.6";

    mc().executeCommand(
        "/summon arrow 4.5 5 2.3 {Tags:[testarrow],NoGravity:1,damage:" + expected + "}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local arrow = Entities.find('@e[tag=testarrow]')[1]\n"//
        + "print(arrow.nbt.damage)\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Double_NBT_is_writable
  @Test
  public void test_Double_NBT_is_writable() throws Exception {
    // Given:
    String expected = "5.6";
    mc().executeCommand("/summon arrow 4.5 5 2.3 {Tags:[testarrow],NoGravity:1,damage:4.3}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local arrow = Entities.find('@e[tag=testarrow]')[1]\n"//
        + "arrow.nbt.damage = " + expected + "\n"//
        + "print(arrow.nbt.damage)\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Double_NBT_List_is_readable
  @Test
  public void test_Double_NBT_List_is_readable() throws Exception {
    // Given:
    Vec3d pos = new Vec3d(4.5, 5, 2.3);
    String expected = String.format("{ %s, %s, %s }", pos.x, pos.y, pos.z);

    mc().executeCommand("/summon pig " + pos.x + " " + pos.y + " " + pos.z
        + " {Tags:[testpig],NoAI:1,NoGravity:1}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local pig = Entities.find('@e[tag=testpig]')[1]\n"//
        + "print(str(pig.nbt.Pos))\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Double_NBT_List_is_writable
  @Test
  public void test_Double_NBT_List_is_writable() throws Exception {
    // Given:
    String expected = "{ 5.5, 6.0, 3.7 }";
    mc().executeCommand("/summon pig 4.5 5 2.3 {Tags:[testpig],NoAI:1,NoGravity:1}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local pig = Entities.find('@e[tag=testpig]')[1]\n"//
        + "pig.nbt.Pos = " + expected + "\n"//
        + "print(str(pig.nbt.Pos))\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Double_NBT_List_Content_is_readable
  @Test
  public void test_Double_NBT_List_Content_is_readable() throws Exception {
    // Given:
    String expected = "5.6";

    mc().executeCommand("/summon pig 4.5 " + expected + " 2.3 {Tags:[testpig],NoAI:1,NoGravity:1}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local pig = Entities.find('@e[tag=testpig]')[1]\n"//
        + "print(pig.nbt.Pos[2])\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Double_NBT_List_Content_is_writable
  @Test
  public void test_Double_NBT_List_Content_is_writable() throws Exception {
    // Given:
    String expected = "5.6";
    mc().executeCommand("/summon pig 4.5 5 2.3 {Tags:[testpig],NoAI:1,NoGravity:1}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local pig = Entities.find('@e[tag=testpig]')[1]\n"//
        + "pig.nbt.Pos[2] = " + expected + "\n"//
        + "print(pig.nbt.Pos[2])\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Double_NBT_List_Content_cannot_be_set_to_String
  @Test
  public void test_Double_NBT_List_Content_cannot_be_set_to_String() throws Exception {
    // Given:
    mc().executeCommand("/summon pig 4.5 5 2.3 {Tags:[testpig],NoAI:1,NoGravity:1}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local pig = Entities.find('@e[tag=testpig]')[1]\n"//
        + "pig.nbt.Pos[2] = 'bla'\n"//
        + "print(pig.nbt.Pos[2])\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).startsWith(
        "Error during spell execution: attempt to write string to number nbt path 'Pos[2]'");
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_setting_custom_NBT_causes_Error
  @Test
  public void test_setting_custom_NBT_causes_Error() throws Exception {
    // Given:
    mc().executeCommand("/summon pig 0 5 0 {Tags:[testpig],NoAI:1,NoGravity:1}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local pig = Entities.find('@e[tag=testpig]')[1]\n"//
        + "pig.nbt.custom = 'bla'\n"//
        + "print(str(pig.nbt.custom))\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage())
        .startsWith("Error during spell execution: attempt to write to unknown nbt path 'custom'");
  }

}
