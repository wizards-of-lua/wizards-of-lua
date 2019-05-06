package net.wizardsoflua.tests;

import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.junit.DisabledOnDist;

public class PlayerTest extends WolTestBase {
  private static final String DEMOMODULE = "my.demomodule";
  private static final String SHAREDMODULE = "somewhere.sharedmodule";

  private static final String PROFILE = "profile";
  private static final String SHARED_PROFILE = "shared-profile";

  BlockPos playerPos = new BlockPos(0, 4, 0);

  @BeforeEach
  public void before() {
    mc().player().setPosition(playerPos);
  }

  @AfterEach
  public void after() throws IOException {
    mc().deleteTeams();
    mc().player().deleteModule(DEMOMODULE);
    mc().player().deleteModule(PROFILE);
    mc().deleteSharedModule(SHAREDMODULE);
    mc().deleteSharedModule(SHARED_PROFILE);
    mc().clearWizardConfigs();
    mc().player().setMainHandItem(null);
    mc().player().setOffHandItem(null);
    mc().player().setHealth(20.0f);
  }

  // /test net.wizardsoflua.tests.PlayerTest test_putNbt_is_not_supported
  @Test
  public void test_putNbt_is_not_supported() throws Exception {
    // Given:

    // When:
    mc().player().chat("/lua spell.owner:putNbt({})");

    // Then:
    assertThat(mc().nextClientMessage()).contains("Error").contains("not supported");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_team_is_readable
  @Test
  public void test_team_is_readable() throws Exception {
    // Given:
    String team = "demo1";
    mc().createTeam(team);
    mc().player().setTeam(team);

    // When:
    mc().player().chat("/lua p=spell.owner; print(p.team)");

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo(team);
  }

  // /test net.wizardsoflua.tests.PlayerTest test_team_is_writable
  @Test
  public void test_team_is_writable() throws Exception {
    // Given:
    String team = "demo2";
    mc().createTeam(team);

    // When:
    mc().player().chat("/lua p=spell.owner; p.team='%s'; print('ok')", team);

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("ok");
    assertThat(mc().player().getTeam()).isEqualTo(team);
  }

  // /test net.wizardsoflua.tests.PlayerTest test_require_player_module
  @Test
  public void test_require_player_module() throws Exception {
    // Given:
    mc().player().createModule(DEMOMODULE, "function dummy() print('hello') end");

    // When:
    mc().player().chat("/lua require('%s'); dummy();", DEMOMODULE);

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("hello");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_require_shared_module
  @Test
  public void test_require_shared_module() throws Exception {
    // Given:
    mc().createSharedModule(SHAREDMODULE, "function shareddummy() print('world!') end");

    // When:
    mc().player().chat("/lua require('%s'); shareddummy();", SHAREDMODULE);

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("world!");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_cast_spell_with_profile
  @Test
  public void test_cast_spell_with_profile() throws Exception {
    // Given:
    mc().player().createModule(PROFILE, "function dummy() print('hello') end");

    // When:
    mc().player().chat("/lua dummy();");

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("hello");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_cast_spell_with_shared_profile
  @Test
  public void test_cast_spell_with_shared_profile() throws Exception {
    // Given:
    mc().createSharedModule(SHARED_PROFILE, "function shareddummy() print('world!') end");

    // When:
    mc().player().chat("/lua shareddummy();");

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("world!");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_mainhand_is_readable
  @Test
  public void test_mainhand_is_readable() throws Exception {
    // Given:
    ItemStack item = new ItemStack(Items.DIAMOND_AXE);
    mc().player().setMainHandItem(item);
    String expected = item.getDisplayName().getString();

    // When:
    mc().player().chat("/lua p=spell.owner; print(p.mainhand.displayName)");

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.PlayerTest test_mainhand_is_writable
  @Test
  public void test_mainhand_is_writable() throws Exception {
    // Given:
    ResourceLocation expected = Items.DIAMOND_AXE.getRegistryName();

    // When:
    mc().player().chat("/lua p=spell.owner; i=Items.get('diamond_axe'); p.mainhand=i; print('ok')");

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("ok");
    assertThat(mc().player().getMainHandItem().getItem().getRegistryName()).isEqualTo(expected);
  }

  // / test net.wizardsoflua.tests.PlayerTest test_offhand_is_readable
  @Test
  public void test_offhand_is_readable() throws Exception {
    // Given:
    ItemStack item = new ItemStack(Items.DIAMOND_AXE);
    mc().player().setOffHandItem(item);
    String expected = item.getDisplayName().getString();

    // When:
    mc().player().chat("/lua p=spell.owner; print(p.offhand.displayName)");

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.PlayerTest test_offhand_is_writable
  @Test
  public void test_offhand_is_writable() throws Exception {
    // Given:
    ResourceLocation expected = Items.DIAMOND_AXE.getRegistryName();

    // When:
    mc().player().chat("/lua p=spell.owner; i=Items.get('diamond_axe'); p.offhand=i; print('ok')");

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("ok");
    assertThat(mc().player().getOffHandItem().getItem().getRegistryName()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.PlayerTest test_sneaking_is_readable
  @Test
  public void test_sneaking_is_readable() throws Exception {
    // Given:

    // When:
    mc().player().chat("/lua p=spell.owner; print(p.sneaking)");

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("false");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_can_set_rotationYaw_to_float_value
  @Test
  public void test_can_set_rotationYaw_to_float_value() throws Exception {
    // Given:

    // When:
    mc().player().chat("/lua spell.owner.rotationYaw = 180.1; print('ok')");

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("ok");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_entityType_is_player
  @Test
  public void test_entityType_is_player() throws Exception {
    // Given:

    // When:
    mc().player().chat("/lua p=spell.owner; print(p.entityType)");

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("player");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_invisible_is_false
  @Test
  public void test_invisible_is_false() throws Exception {
    // Given:

    // When:
    mc().player().chat("/lua p=spell.owner; print(p.invisible)");

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("false");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_invisible_is_true
  @Test
  public void test_invisible_is_true() throws Exception {
    // Given:
    String playerName = mc().player().getName();
    mc().player().chat("/effect give %s minecraft:invisibility 10 1", playerName);
    assertThat(mc().nextClientMessage()).isEqualTo("Applied effect Invisibility to " + playerName);

    // When:
    mc().player().chat("/lua p=spell.owner; print(p.invisible)");

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("true");

    mc().player().chat("/effect clear %s", playerName);
  }

  // /test net.wizardsoflua.tests.PlayerTest test_health_is_readable
  @Test
  public void test_health_is_readable() throws Exception {
    // Given
    String expected = format(mc().player().getHealth());

    // When:
    mc().player().chat("/lua p=spell.owner; print(p.health)");

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.PlayerTest test_health_is_readable
  @Test
  public void test_health_is_writable() throws Exception {
    // Given

    // When:
    mc().player().chat("/lua p=spell.owner; p.health=5.5; print(p.health)");

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("5.5");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_sprinting_is_readable
  @Test
  public void test_sprinting_is_readable() throws Exception {
    // Given:

    // When:
    mc().player().chat("/lua p=spell.owner; print(p.sprinting)");

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("false");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_operator_returns_true
  @Test
  public void test_operator_returns_true() throws Exception {
    // Given:
    mc().player().setOperator(true);

    // When:
    mc().player().chat("/lua p=spell.owner; print(p.operator)");

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_operator_returns_false
  @Test
  @DisabledOnDist(value = Dist.CLIENT,
      reason = "In single player you don't have to be op to execute commands")
  public void test_operator_returns_false() throws Exception {
    // Given:
    mc().player().setOperator(false);
    mc().clearEvents();

    // When:
    mc().executeCommand("/lua p=Entities.find('@a[name=%s]')[1]; print(p.operator)",
        mc().player().getName());

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("false");
  }

}
