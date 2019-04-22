package net.wizardsoflua.tests;

import static java.lang.String.valueOf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

public class WorldTest extends WolTestBase {

  EnumDifficulty oldDifficulty;

  @BeforeEach
  public void before() {
    oldDifficulty = mc().getDifficulty();
  }

  @AfterEach
  public void after() {
    mc().setDifficulty(oldDifficulty);
    // clear door
    BlockPos pos = mc().getWorldSpawnPoint();
    mc().setBlock(pos.up().up(), Blocks.AIR);
    mc().setBlock(pos.up().up().north(), Blocks.AIR);
    mc().setBlock(pos, Blocks.AIR);
    mc().setBlock(pos.up(), Blocks.AIR);
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

  // /test net.wizardsoflua.tests.WorldTest test_world_folder_is_readable
  @Test
  public void test_world_folder_is_readable() {
    // Given:
    String expected = mc().getWorldFolderName();

    // When:
    mc().executeCommand("/lua w=spell.world; print(w.folder)");

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
    mc().executeCommand("/lua w=spell.world; w.difficulty='%s'; print(w.difficulty)",
        expected.name());

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

  // /test net.wizardsoflua.tests.WorldTest test_getNearestVillage
  @Test
  public void test_getNearestVillage() {
    // Given:
    BlockPos pos = mc().getWorldSpawnPoint();

    mc().setBlock(pos.up().up(), Blocks.GRASS);
    mc().setBlock(pos.up().up().north(), Blocks.GRASS);
    mc().setBlock(pos, Blocks.DARK_OAK_DOOR);
    mc().setBlock(pos.up(), Blocks.DARK_OAK_DOOR);
    mc().executeCommand("/summon minecraft:villager %s %s %s", pos.getX(), pos.getY(), pos.getZ());

    sleep(50 * 5); // wait for at least 5 game ticks so that the new villlage can be detected

    mc().clearEvents();

    BlockPos center = mc().getNearestVillageCenter(pos, 1);
    String expected = center == null ? "nil" : format(center);

    // When:
    mc().executeCommand(
        "/lua spell.pos=Vec3(%s,%s,%s); v=spell.world:getNearestVillage(spell.pos, 1); print(v);",
        pos.getX(), pos.getY(), pos.getZ());

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WorldTest test_world_isLoadedAt_returns_true_at_player_location
  @Test
  public void test_world_isLoadedAt_returns_true_at_player_location() {
    // Given:
    String expected = "true";

    // When:
    mc().player().chat("/lua v=spell.pos; w=spell.world; b=w:isLoadedAt(v); print(b)");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WorldTest test_world_isLoadedAt_returns_false_far_away
  @Test
  public void test_world_isLoadedAt_returns_false_far_away() {
    // Given:
    String expected = "false";

    // When:
    mc().player()
        .chat("/lua v=spell.pos+Vec3(10000,0,0); w=spell.world; b=w:isLoadedAt(v); print(b)");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WorldTest test_world_isGeneratedAt_returns_true_at_player_location
  @Test
  public void test_world_isGeneratedAt_returns_true_at_player_location() {
    // Given:
    String expected = "true";

    // When:
    mc().player().chat("/lua v=spell.pos; w=spell.world; b=w:isGeneratedAt(v); print(b)");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WorldTest test_world_isGeneratedAt_returns_false_far_away
  @Test
  public void test_world_isGeneratedAt_returns_false_far_away() {
    // Given:
    String expected = "false";

    // When:
    mc().player().chat(
        "/lua v=spell.pos+Vec3(9999999,0,9999999); w=spell.world; b=w:isGeneratedAt(v); print(b)");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WorldTest test_time_is_readable
  @Test
  public void test_time_is_readable() throws Exception {
    // Given:
    mc().gameRules().setDoDaylightCycle(false);
    long expected = 1999;
    mc().setWorldTime(expected);

    // When:
    mc().executeCommand("/lua print(spell.world.time)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(valueOf(expected));
  }

  // /test net.wizardsoflua.tests.WorldTest test_time_is_writable
  @Test
  public void test_time_is_writable() throws Exception {
    // Given:
    mc().gameRules().setDoDaylightCycle(false);
    long expected = 2929;

    // When:
    mc().executeCommand("/lua spell.world.time=%s; print('ok')", expected);
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");

    // Then:
    long actual = mc().getWorldtime();
    assertThat(actual).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.WorldTest test_daytime_is_readable
  @Test
  public void test_daytime_is_readable() throws Exception {
    // Given:
    mc().gameRules().setDoDaylightCycle(false);
    long expected = 3939;
    mc().setWorldTime(24000 + expected);

    // When:
    mc().executeCommand("/lua print(spell.world.daytime)");

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(valueOf(expected));
  }
}
