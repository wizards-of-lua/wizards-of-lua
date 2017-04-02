package net.karneim.luamod.cursor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.mockito.Mockito;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.Rotation;
import net.minecraft.world.World;

public class Spell_rotate_Test extends TestBase {

  private World world = Mockito.mock(World.class);
  private ICommandSender commandSender = Mockito.mock(ICommandSender.class);
  private Spell underTest = newSpell(commandSender, world);


  @Test
  public void test_rotate_Left() {
    // Given:
    underTest.setRotation(Rotation.CLOCKWISE_90);

    // When:
    underTest.rotate(Rotation.COUNTERCLOCKWISE_90);

    // Then:
    assertThat(underTest.getRotation()).isEqualTo(0f);
  }

  @Test
  public void test_rotate_Right() {
    // Given:
    underTest.setRotation(Rotation.CLOCKWISE_90);

    // When:
    underTest.rotate(Rotation.CLOCKWISE_90);

    // Then:
    assertThat(underTest.getRotation()).isEqualTo(-180f);
  }

  @Test
  public void test_rotate_Forward() {
    // Given:
    underTest.setRotation(Rotation.CLOCKWISE_90);

    // When:
    underTest.rotate(Rotation.NONE);

    // Then:
    assertThat(underTest.getRotation()).isEqualTo(90f);
  }

  @Test
  public void test_rotate_Back() {
    // Given:
    underTest.setRotation(Rotation.CLOCKWISE_90);

    // When:
    underTest.rotate(Rotation.CLOCKWISE_180);

    // Then:
    assertThat(underTest.getRotation()).isEqualTo(-90);
  }
  
  @Test
  public void test_rotate_45() {
    // Given:
    underTest.setRotation(0);

    // When:
    underTest.rotate(45);

    // Then:
    assertThat(underTest.getRotation()).isEqualTo(45f);
  }
  
  @Test
  public void test_rotate_45_45() {
    // Given:
    underTest.setRotation(45);

    // When:
    underTest.rotate(45);

    // Then:
    assertThat(underTest.getRotation()).isEqualTo(90f);
  }
  
  @Test
  public void test_rotate_minus_45_45() {
    // Given:
    underTest.setRotation(-45);

    // When:
    underTest.rotate(45);

    // Then:
    assertThat(underTest.getRotation()).isEqualTo(0f);
  }
}
