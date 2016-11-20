package net.karneim.luamod.cursor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;

public class EnumDirection_North_Test {

  private static final Rotation rotation = Rotation.NONE;

  @Test
  public void testRotate_Forward() {
    // Given:
    EnumDirection underTest = EnumDirection.FORWARD;

    // When:
    EnumFacing act = underTest.rotate(rotation);

    // Then:
    assertThat(act).isEqualTo(EnumFacing.NORTH);
  }

  @Test
  public void testRotate_Left() {
    // Given:
    EnumDirection underTest = EnumDirection.LEFT;

    // When:
    EnumFacing act = underTest.rotate(rotation);

    // Then:
    assertThat(act).isEqualTo(EnumFacing.WEST);
  }

  @Test
  public void testRotate_Right() {
    // Given:
    EnumDirection underTest = EnumDirection.RIGHT;

    // When:
    EnumFacing act = underTest.rotate(rotation);

    // Then:
    assertThat(act).isEqualTo(EnumFacing.EAST);
  }

  @Test
  public void testRotate_Back() {
    // Given:
    EnumDirection underTest = EnumDirection.BACK;

    // When:
    EnumFacing act = underTest.rotate(rotation);

    // Then:
    assertThat(act).isEqualTo(EnumFacing.SOUTH);
  }

  @Test
  public void testRotate_Up() {
    // Given:
    EnumDirection underTest = EnumDirection.UP;

    // When:
    EnumFacing act = underTest.rotate(rotation);

    // Then:
    assertThat(act).isEqualTo(EnumFacing.UP);
  }

  @Test
  public void testRotate_Down() {
    // Given:
    EnumDirection underTest = EnumDirection.DOWN;

    // When:
    EnumFacing act = underTest.rotate(rotation);

    // Then:
    assertThat(act).isEqualTo(EnumFacing.DOWN);
  }

}
