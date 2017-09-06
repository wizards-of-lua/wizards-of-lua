package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.util.math.Vec3d;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class EntityTest extends WolTestBase {

  // /test net.wizardsoflua.tests.EntityTest test_nbt_is_not_nil
  @Test
  public void test_nbt_is_not_nil() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua print(spell.nbt~=nil)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.EntityTest test_nbt_pos_is_readable
  @Test
  public void test_nbt_pos_is_readable() throws Exception {
    // Given:
    Vec3d pos = new Vec3d(1, 2, 3);
    String expected = String.format("{ %s, %s, %s }", pos.xCoord, pos.yCoord, pos.zCoord);
    // When:
    mc().executeCommand("/lua spell.pos=Vec3(%s,%s,%s); print(str(spell.nbt.Pos))", pos.xCoord,
        pos.yCoord, pos.zCoord);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.EntityTest test_nbt_is_not_writable
  @Test
  public void test_nbt_is_not_writable() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua spell.nbt = {};");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).contains("Exception").contains("property is readonly");
  }

  // /test net.wizardsoflua.tests.EntityTest test_putNbt_setting_pos
  @Test
  public void test_putNbt_setting_pos() throws Exception {
    // Given:
    Vec3d posA = new Vec3d(1, 2, 3);
    Vec3d posB = new Vec3d(5, 6, 7);
    String expected = format(posB);
    // When:
    mc().executeCommand(
        "/lua spell.pos=Vec3(%s,%s,%s); spell:putNbt({Pos={%s, %s, %s}}); print(spell.pos)",
        posA.xCoord, posA.yCoord, posA.zCoord, posB.xCoord, posB.yCoord, posB.zCoord);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

}
