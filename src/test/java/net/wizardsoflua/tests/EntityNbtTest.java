package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import net.minecraft.util.math.Vec3d;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class EntityNbtTest extends WolTestBase {

  // /test net.wizardsoflua.tests.EntityNbtTest test_Boolean_NBT_is_readable
  @Test
  public void test_Boolean_NBT_is_readable() throws Exception {
    // Given:
    String expected = "1";
    mc().executeCommand("/summon creeper 4.5 5 2.3 {Tags:[testcreeper],NoAI:1,NoGravity:1,powered:"
        + expected + "}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local creeper = Entities.find('@e[tag=testcreeper]')[1]\n"//
        + "print(creeper.nbt.powered)\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Boolean_NBT_is_writable
  @Test
  public void test_Boolean_NBT_is_writable() throws Exception {
    // Given:
    String expected = "1";
    mc().executeCommand(
        "/summon creeper 4.5 5 2.3 {Tags:[testcreeper],NoAI:1,NoGravity:1,powered:0}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local creeper = Entities.find('@e[tag=testcreeper]')[1]\n"//
        + "creeper.nbt.powered = " + expected + "\n"//
        + "print(creeper.nbt.powered)\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Byte_NBT_is_readable
  @Test
  public void test_Byte_NBT_is_readable() throws Exception {
    // Given:
    String expected = "5";
    mc().executeCommand(
        "/summon creeper 4.5 5 2.3 {Tags:[testcreeper],NoAI:1,NoGravity:1,ExplosionRadius:"
            + expected + "}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local creeper = Entities.find('@e[tag=testcreeper]')[1]\n"//
        + "print(creeper.nbt.ExplosionRadius)\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Byte_NBT_is_writable
  @Test
  public void test_Byte_NBT_is_writable() throws Exception {
    // Given:
    String expected = "5";
    mc().executeCommand(
        "/summon creeper 4.5 5 2.3 {Tags:[testcreeper],NoAI:1,NoGravity:1,ExplosionRadius:4}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local creeper = Entities.find('@e[tag=testcreeper]')[1]\n"//
        + "creeper.nbt.ExplosionRadius = " + expected + "\n"//
        + "print(creeper.nbt.ExplosionRadius)\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Double_NBT_is_readable
  @Test
  public void test_Double_NBT_is_readable() throws Exception {
    // Given:
    String expected = "5.625";
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
    String expected = "5.625";
    mc().executeCommand("/summon arrow 4.5 5 2.3 {Tags:[testarrow],NoGravity:1,damage:4.25}");
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

  // /test net.wizardsoflua.tests.EntityNbtTest test_Float_NBT_is_readable
  @Test
  public void test_Float_NBT_is_readable() throws Exception {
    // Given:
    String expected = "5.625";
    mc().executeCommand(
        "/summon pig 4.5 5 2.3 {Tags:[testpig],NoAI:1,NoGravity:1,Health:" + expected + "}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local pig = Entities.find('@e[tag=testpig]')[1]\n"//
        + "print(pig.nbt.Health)\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Float_NBT_is_writable
  @Test
  public void test_Float_NBT_is_writable() throws Exception {
    // Given:
    String expected = "5.625";
    mc().executeCommand("/summon pig 4.5 5 2.3 {Tags:[testpig],NoAI:1,NoGravity:1,Health:4.25}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local pig = Entities.find('@e[tag=testpig]')[1]\n"//
        + "pig.nbt.Health = " + expected + "\n"//
        + "print(pig.nbt.Health)\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Integer_NBT_is_readable
  @Test
  public void test_Integer_NBT_is_readable() throws Exception {
    // Given:
    String expected = "5";
    mc().executeCommand(
        "/summon slime 4.5 5 2.3 {Tags:[testslime],NoAI:1,NoGravity:1,Size:" + expected + "}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local slime = Entities.find('@e[tag=testslime]')[1]\n"//
        + "print(slime.nbt.Size)\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Integer_NBT_is_writable
  @Test
  public void test_Integer_NBT_is_writable() throws Exception {
    // Given:
    String expected = "5";
    mc().executeCommand("/summon slime 4.5 5 2.3 {Tags:[testslime],NoAI:1,NoGravity:1,Size:4}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local slime = Entities.find('@e[tag=testslime]')[1]\n"//
        + "slime.nbt.Size = " + expected + "\n"//
        + "print(slime.nbt.Size)\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Long_NBT_is_readable
  @Test
  public void test_Long_NBT_is_readable() throws Exception {
    // Given:
    String expected = "5";
    mc().executeCommand(
        "/summon pig 4.5 5 2.3 {Tags:[testpig],NoAI:1,NoGravity:1,DeathLootTable:\"\",DeathLootTableSeed:"
            + expected + "}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local pig = Entities.find('@e[tag=testpig]')[1]\n"//
        + "print(pig.nbt.DeathLootTableSeed)\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Long_NBT_is_writable
  @Test
  public void test_Long_NBT_is_writable() throws Exception {
    // Given:
    String expected = "5";
    mc().executeCommand(
        "/summon pig 4.5 5 2.3 {Tags:[testpig],NoAI:1,NoGravity:1,DeathLootTable:\"\",DeathLootTableSeed:4}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local pig = Entities.find('@e[tag=testpig]')[1]\n"//
        + "pig.nbt.DeathLootTableSeed = " + expected + "\n"//
        + "print(pig.nbt.DeathLootTableSeed)\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Short_NBT_is_readable
  @Test
  public void test_Short_NBT_is_readable() throws Exception {
    // Given:
    String expected = "5";
    mc().executeCommand(
        "/summon zombie_pigman 4.5 5 2.3 {Tags:[testzombie_pigman],NoAI:1,NoGravity:1,Anger:"
            + expected + "}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local zombie_pigman = Entities.find('@e[tag=testzombie_pigman]')[1]\n"//
        + "print(zombie_pigman.nbt.Anger)\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Short_NBT_is_writable
  @Test
  public void test_Short_NBT_is_writable() throws Exception {
    // Given:
    String expected = "5";
    mc().executeCommand(
        "/summon zombie_pigman 4.5 5 2.3 {Tags:[testzombie_pigman],NoAI:1,NoGravity:1,Anger:4}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local zombie_pigman = Entities.find('@e[tag=testzombie_pigman]')[1]\n"//
        + "zombie_pigman.nbt.Anger = " + expected + "\n"//
        + "print(zombie_pigman.nbt.Anger)\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_String_NBT_is_readable
  @Test
  public void test_String_NBT_is_readable() throws Exception {
    // Given:
    String expected = "bli";
    mc().executeCommand(
        "/summon pig 4.5 5 2.3 {Tags:[testpig],NoAI:1,NoGravity:1,CustomName:" + expected + "}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local pig = Entities.find('@e[tag=testpig]')[1]\n"//
        + "print(pig.nbt.CustomName)\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_String_NBT_is_writable
  @Test
  public void test_String_NBT_is_writable() throws Exception {
    // Given:
    String expected = "bli";
    mc().executeCommand("/summon pig 4.5 5 2.3 {Tags:[testpig],NoAI:1,NoGravity:1,CustomName:bla}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local pig = Entities.find('@e[tag=testpig]')[1]\n"//
        + "pig.nbt.CustomName = " + expected + "\n"//
        + "print(pig.nbt.CustomName)\n"//
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

  // /test net.wizardsoflua.tests.EntityNbtTest test_can_write_Integer_Table_to_Double_NBT_List
  @Test
  public void test_can_write_Integer_Table_to_Double_NBT_List() throws Exception {
    // Given:
    mc().executeCommand("/summon pig 4.5 5 2.3 {Tags:[testpig],NoAI:1,NoGravity:1}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local pig = Entities.find('@e[tag=testpig]')[1]\n"//
        + "pig.nbt.Pos = { 5, 6, 3 }\n"//
        + "print(str(pig.nbt.Pos))\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("{ 5.0, 6.0, 3.0 }");
  }

  // /test net.wizardsoflua.tests.EntityNbtTest test_Double_NBT_List_Content_is_readable
  @Test
  public void test_Double_NBT_List_Content_is_readable() throws Exception {
    // Given:
    String expected = "5.625";
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
    String expected = "5.625";
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


  // @formatter:off /test net.wizardsoflua.tests.EntityNbtTest test_Double_NBT_List_Content_can_be_set_to_an_Integer @formatter:on
  @Test
  public void test_Double_NBT_List_Content_can_be_set_to_an_Integer() throws Exception {
    // Given:
    mc().executeCommand("/summon pig 4.5 5 2.3 {Tags:[testpig],NoAI:1,NoGravity:1}");
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua local pig = Entities.find('@e[tag=testpig]')[1]\n"//
        + "pig.nbt.Pos[2] = 6\n"//
        + "print(pig.nbt.Pos[2])\n"//
    );

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("6.0");
  }

  // @formatter:off /test net.wizardsoflua.tests.EntityNbtTest test_Double_NBT_List_Content_cannot_be_set_to_a_String @formatter:on
  @Test
  public void test_Double_NBT_List_Content_cannot_be_set_to_a_String() throws Exception {
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
