package net.wizardsoflua.tests;

import org.assertj.core.data.Offset;
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

  // / test net.wizardsoflua.tests.Vec3Test test_check_with_Vec3
  @Test
  public void test_check_with_Vec3() throws Exception {
    // Given:

    // When:
    mc().executeCommand("/lua v=Vec3.from(1,1,1); Check.isVec3(v); print('ok')");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
  }

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

  // /test net.wizardsoflua.tests.Vec3Test test_new_creates_new_Vec3
  @Test
  public void test_new_creates_new_Vec3() throws Exception {
    // Given:
    int x = 1;
    int y = 2;
    int z = 3;

    // When:
    mc().executeCommand("/lua v=Vec3.new({x=%s,y=%s,z=%s}); print(v)", x, y, z);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("{1, 2, 3}");
  }

  // /test net.wizardsoflua.tests.Vec3Test test_tostring
  @Test
  public void test_tostring() throws Exception {
    // Given:
    int x = 1;
    int y = 2;
    int z = 3;

    // When:
    mc().executeCommand("/lua v=Vec3.from(%s,%s,%s); print(v:tostring())", x, y, z);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("{1, 2, 3}");
  }

  // /test net.wizardsoflua.tests.Vec3Test test_add
  @Test
  public void test_add() throws Exception {
    // Given:
    int ax = 1;
    int ay = 2;
    int az = 3;
    int bx = 4;
    int by = 6;
    int bz = 8;

    // When:
    mc().executeCommand("/lua a=Vec3.from(%s,%s,%s); b=Vec3.from(%s,%s,%s); print(a:add(b))", ax,
        ay, az, bx, by, bz);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("{5, 8, 11}");
  }

  // /test net.wizardsoflua.tests.Vec3Test test_add_meta
  @Test
  public void test_add_meta() throws Exception {
    // Given:
    int ax = 1;
    int ay = 2;
    int az = 3;
    int bx = 4;
    int by = 6;
    int bz = 8;

    // When:
    mc().executeCommand("/lua a=Vec3.from(%s,%s,%s); b=Vec3.from(%s,%s,%s); print(a+b)", ax, ay, az,
        bx, by, bz);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("{5, 8, 11}");
  }

  // /test net.wizardsoflua.tests.Vec3Test test_substract
  @Test
  public void test_substract() throws Exception {
    // Given:
    int ax = 5;
    int ay = 4;
    int az = 3;
    int bx = 2;
    int by = 4;
    int bz = 6;

    // When:
    mc().executeCommand("/lua a=Vec3.from(%s,%s,%s); b=Vec3.from(%s,%s,%s); print(a:substract(b))",
        ax, ay, az, bx, by, bz);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("{3, 0, -3}");
  }

  // /test net.wizardsoflua.tests.Vec3Test test_substract_meta
  @Test
  public void test_substract_meta() throws Exception {
    // Given:
    int ax = 5;
    int ay = 4;
    int az = 3;
    int bx = 2;
    int by = 4;
    int bz = 6;

    // When:
    mc().executeCommand("/lua a=Vec3.from(%s,%s,%s); b=Vec3.from(%s,%s,%s); print(a-b)", ax, ay, az,
        bx, by, bz);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("{3, 0, -3}");
  }

  // /test net.wizardsoflua.tests.Vec3Test test_sqrMagnitude
  @Test
  public void test_sqrMagnitude() throws Exception {
    // Given:
    int x = 5;
    int y = 4;
    int z = 3;
    String expected = String.valueOf(x * x + y * y + z * z);

    // When:
    mc().executeCommand("/lua v=Vec3.from(%s,%s,%s); print(v:sqrMagnitude())", x, y, z);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.Vec3Test test_magnitude
  @Test
  public void test_magnitude() throws Exception {
    // Given:
    int x = 5;
    int y = 4;
    int z = 3;
    double expected = Math.sqrt(x * x + y * y + z * z);

    // When:
    mc().executeCommand("/lua v=Vec3.from(%s,%s,%s); print(v:magnitude())", x, y, z);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    double actValue = Double.parseDouble(act.getMessage());
    assertThat(actValue).isEqualTo(expected, Offset.offset(0.01));
  }

  // /test net.wizardsoflua.tests.Vec3Test test_dotProduct
  @Test
  public void test_dotProduct() throws Exception {
    // Given:
    int ax = 2;
    int ay = 3;
    int az = 4;
    int bx = 5;
    int by = 6;
    int bz = 7;
    String expected = String.valueOf(ax * bx + ay * by + az * bz);

    // When:
    mc().executeCommand("/lua a=Vec3.from(%s,%s,%s); b=Vec3.from(%s,%s,%s); print(a:dotProduct(b))",
        ax, ay, az, bx, by, bz);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.Vec3Test test_dotProduct_meta
  @Test
  public void test_dotProduct_meta() throws Exception {
    // Given:
    int ax = 2;
    int ay = 3;
    int az = 4;
    int bx = 5;
    int by = 6;
    int bz = 7;
    String expected = String.valueOf(ax * bx + ay * by + az * bz);

    // When:
    mc().executeCommand("/lua a=Vec3.from(%s,%s,%s); b=Vec3.from(%s,%s,%s); print(a*b)", ax, ay, az,
        bx, by, bz);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.Vec3Test test_scale
  @Test
  public void test_scale() throws Exception {
    // Given:
    int x = 5;
    int y = 4;
    int z = 3;
    double f = 2.5d;
    String expected = "{" + x * f + ", " + y * f + ", " + z * f + "}";

    // When:
    mc().executeCommand("/lua v=Vec3.from(%s,%s,%s); print(v:scale(%s))", x, y, z, f);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.Vec3Test test_scale_meta_1
  @Test
  public void test_scale_meta_1() throws Exception {
    // Given:
    int x = 5;
    int y = 4;
    int z = 3;
    double f = 2.5d;
    String expected = "{" + x * f + ", " + y * f + ", " + z * f + "}";

    // When:
    mc().executeCommand("/lua v=Vec3.from(%s,%s,%s); f=%s; print(v*f)", x, y, z, f);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.Vec3Test test_scale_meta_2
  @Test
  public void test_scale_meta_2() throws Exception {
    // Given:
    int x = 5;
    int y = 4;
    int z = 3;
    double f = 2.5d;
    String expected = "{" + x * f + ", " + y * f + ", " + z * f + "}";

    // When:
    mc().executeCommand("/lua v=Vec3.from(%s,%s,%s); f=%s; print(f*v)", x, y, z, f);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.Vec3Test test_invert
  @Test
  public void test_invert() throws Exception {
    // Given:
    int x = 5;
    int y = 4;
    int z = 3;
    String expected = "{" + -x + ", " + -y + ", " + -z + "}";

    // When:
    mc().executeCommand("/lua v=Vec3.from(%s,%s,%s); print(v:invert())", x, y, z);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.Vec3Test test_invert_meta
  @Test
  public void test_invert_meta() throws Exception {
    // Given:
    int x = 5;
    int y = 4;
    int z = 3;
    String expected = "{" + -x + ", " + -y + ", " + -z + "}";

    // When:
    mc().executeCommand("/lua v=Vec3.from(%s,%s,%s); print(-v)", x, y, z);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.Vec3Test test_concat_meta
  @Test
  public void test_concat_meta() throws Exception {
    // Given:
    int ax = 2;
    int ay = 3;
    int az = 4;
    int bx = 5;
    int by = 6;
    int bz = 7;
    String expected =
        "{" + ax + ", " + ay + ", " + az + "}" + "{" + bx + ", " + by + ", " + bz + "}";

    // When:
    mc().executeCommand("/lua a=Vec3.from(%s,%s,%s); b=Vec3.from(%s,%s,%s); print(a..b)", ax, ay,
        az, bx, by, bz);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

}
