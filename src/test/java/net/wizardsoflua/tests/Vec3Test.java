package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

/**
 * Testing the Vec3 Lua module
 */
@RunWith(MinecraftJUnitRunner.class)
public class Vec3Test extends WolTestBase {

  // /test net.wizardsoflua.tests.Vec3Test test_from_creates_new_Vec3
  @Test
  public void test_from_creates_new_Vec3() throws Exception {
    // Given:
    int x = 1;
    int y = 2;
    int z = 3;

    // When:
    mc().executeCommand("/lua v=Vec3.from(%s,%s,%s); print(v)", x, y, z);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("{1, 2, 3}");
  }

  // /test net.wizardsoflua.tests.Vec3Test test_check_with_Vec3
  @Test
  public void test_check_with_Vec3() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua v=Vec3.from(1,1,1); check.Vec3(v); print('ok')");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
  }

}
