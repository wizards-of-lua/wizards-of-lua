package net.wizardsoflua.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.world.EnumDifficulty;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class WorldTest extends WolTestBase {

  EnumDifficulty oldDifficulty;
  
  @Before
  public void before() {
    oldDifficulty = mc().getDifficulty();
  }
  
  @After
  public void after() {
    mc().setDifficulty(oldDifficulty);
  }
  
  // /test net.wizardsoflua.tests.WorldTest test_world_name_is_readable
  @Test
  public void test_world_name_is_readable() {
    // Given:
    String expected = mc().getWorldName();

    // When:
    mc().executeCommand("/lua w=spell.world; print(w.name)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WorldTest test_world_dimension_is_readable
  @Test
  public void test_world_dimension_is_readable() {
    // Given:
    String expected = String.valueOf(mc().getWorldDimension());

    // When:
    mc().executeCommand("/lua w=spell.world; print(w.dimension)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WorldTest test_world_difficulty_is_readable
  @Test
  public void test_world_difficulty_is_readable() {
    // Given:
    String expected = mc().getDifficulty().name();

    // When:
    mc().executeCommand("/lua w=spell.world; print(w.difficulty)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WorldTest test_world_difficulty_is_writable
  @Test
  public void test_world_difficulty_is_writable() {
    // Given:
    EnumDifficulty expected = EnumDifficulty.NORMAL;

    // When:
    mc().executeCommand("/lua w=spell.world; w.difficulty='%s'; print(w.difficulty)", expected.name());

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected.name());
    assertThat(mc().getDifficulty()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WorldTest test_world_spawnPoint_is_readable
  @Test
  public void test_world_spawnPoint_is_readable() {
    // Given:
    String expected = format(mc().getWorldSpawnPoint());

    // When:
    mc().executeCommand("/lua w=spell.world; print(w.spawnPoint)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WorldTest test_world_canSeeSky_in_open_air
  @Test
  public void test_world_canSeeSky_in_open_air() {
    // Given:
    String expected = "true";

    // When:
    mc().executeCommand("/lua v=spell.pos; v.y=256; w=spell.world; b=w:canSeeSky(v); print(b)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WorldTest test_world_canSeeSky_beyond_solid_block
  @Test
  public void test_world_canSeeSky_beyond_solid_block() {
    // Given:
    String expected = "false";

    // When:
    mc().executeCommand(
        "/lua v=spell.pos; v.y=250; spell.pos=v+Vec3(0,2,0); spell.block=Blocks.get('dirt'); ; w=spell.world; b=w:canSeeSky(v); print(b)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }
}
