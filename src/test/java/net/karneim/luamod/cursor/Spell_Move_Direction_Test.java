package net.karneim.luamod.cursor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.mockito.Mockito;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Spell_Move_Direction_Test {

  private World world = Mockito.mock(World.class);
  private ICommandSender commandSender = Mockito.mock(ICommandSender.class);
  private Spell underTest = new Spell(commandSender, world);

  @Test
  public void test_move_Forward() {
    // Given:

    // When:
    underTest.move(EnumDirection.FORWARD);

    // Then:
    assertThat(underTest.getWorldPosition()).isEqualTo(new Vec3d(0, 0, 1));
  }

  @Test
  public void test_move_Right() {
    // Given:

    // When:
    underTest.move(EnumDirection.RIGHT);

    // Then:
    assertThat(underTest.getWorldPosition()).isEqualTo(new Vec3d(-1, 0, 0));
  }

  @Test
  public void test_move_Back() {
    // Given:

    // When:
    underTest.move(EnumDirection.BACK);

    // Then:
    assertThat(underTest.getWorldPosition()).isEqualTo(new Vec3d(0, 0, -1));
  }

  @Test
  public void test_move_Left() {
    // Given:

    // When:
    underTest.move(EnumDirection.LEFT);

    // Then:
    assertThat(underTest.getWorldPosition()).isEqualTo(new Vec3d(1, 0, 0));
  }
}
