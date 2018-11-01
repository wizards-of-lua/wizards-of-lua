package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import net.minecraft.util.math.Vec3d;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class EntityNbtTest extends WolTestBase {

  // /test net.wizardsoflua.tests.EntityNbtTest test_nbt_Pos_is_readable
  @Test
  public void test_nbt_Pos_is_readable() throws Exception {
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

  // /test net.wizardsoflua.tests.EntityNbtTest test_nbt_Pos_is_writable
  @Test
  public void test_nbt_Pos_is_writable() throws Exception {
    // Given:
    String expected = "{5.5, 6, 3.7}";
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

}
