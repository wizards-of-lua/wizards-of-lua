package net.wizardsoflua.tests;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

@RunWith(MinecraftJUnitRunner.class)
public class PlayerTest extends WolTestBase {
  private static final String DEMOMODULE = "my.demomodule";
  private static final String SHAREDMODULE = "somewhere.sharedmodule";

  private static final String PROFILE = "profile";
  private static final String SHARED_PROFILE = "shared-profile";

  BlockPos playerPos = new BlockPos(0, 4, 0);

  @Before
  public void setPlayerPos() {
    mc().player().setPosition(playerPos);
  }

  @After
  public void after() throws IOException {
    mc().deleteTeams();
    mc().player().deleteModule(DEMOMODULE);
    mc().player().deleteModule(PROFILE);
    mc().deleteSharedModule(SHAREDMODULE);
    mc().deleteSharedModule(SHARED_PROFILE);
    mc().clearWizardConfigs();
    mc().player().setMainHandItem(null);
    mc().player().setOffHandItem(null);
  }

  // /test net.wizardsoflua.tests.PlayerTest test_putNbt_is_not_supported
  @Test
  public void test_putNbt_is_not_supported() throws Exception {
    // Given:

    // When:
    mc().player().chat("/lua spell.owner:putNbt({})");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).contains("Error").contains("not supported");
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
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(team);
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
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
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
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("hello");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_require_shared_module
  @Test
  public void test_require_shared_module() throws Exception {
    // Given:
    mc().createSharedModule(SHAREDMODULE, "function shareddummy() print('world!') end");

    // When:
    mc().player().chat("/lua require('%s'); shareddummy();", SHAREDMODULE);

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("world!");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_cast_spell_with_profile
  @Test
  public void test_cast_spell_with_profile() throws Exception {
    // Given:
    mc().player().createModule(PROFILE, "function dummy() print('hello') end");

    // When:
    mc().player().chat("/lua dummy();");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("hello");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_cast_spell_with_shared_profile
  @Test
  public void test_cast_spell_with_shared_profile() throws Exception {
    // Given:
    mc().createSharedModule(SHARED_PROFILE, "function shareddummy() print('world!') end");

    // When:
    mc().player().chat("/lua shareddummy();");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("world!");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_mainhand_is_readable
  @Test
  public void test_mainhand_is_readable() throws Exception {
    // Given:
    ItemStack item = new ItemStack(Items.DIAMOND_AXE);
    mc().player().setMainHandItem(item);
    String expected = item.getDisplayName();

    // When:
    mc().player().chat("/lua p=spell.owner; print(p.mainhand.displayName)");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.PlayerTest test_mainhand_is_writable
  @Test
  public void test_mainhand_is_writable() throws Exception {
    // Given:
    ResourceLocation expected = Items.DIAMOND_AXE.getRegistryName();

    // When:
    mc().player().chat("/lua p=spell.owner; i=Items.get('diamond_axe'); p.mainhand=i; print('ok')");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
    assertThat(mc().player().getMainHandItem().getItem().getRegistryName()).isEqualTo(expected);
  }

  // / test net.wizardsoflua.tests.PlayerTest test_offhand_is_readable
  @Test
  public void test_offhand_is_readable() throws Exception {
    // Given:
    ItemStack item = new ItemStack(Items.DIAMOND_AXE);
    mc().player().setOffHandItem(item);
    String expected = item.getDisplayName();

    // When:
    mc().player().chat("/lua p=spell.owner; print(p.offhand.displayName)");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.PlayerTest test_offhand_is_writable
  @Test
  public void test_offhand_is_writable() throws Exception {
    // Given:
    ResourceLocation expected = Items.DIAMOND_AXE.getRegistryName();

    // When:
    mc().player().chat("/lua p=spell.owner; i=Items.get('diamond_axe'); p.offhand=i; print('ok')");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
    assertThat(mc().player().getOffHandItem().getItem().getRegistryName()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.PlayerTest test_sneaking_is_readable
  @Test
  public void test_sneaking_is_readable() throws Exception {
    // Given:

    // When:
    mc().player().chat("/lua p=spell.owner; print(p.sneaking)");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("false");
  }

  // /test net.wizardsoflua.tests.PlayerTest test_can_set_rotationYaw_to_float_value
  @Test
  public void test_can_set_rotationYaw_to_float_value() throws Exception {
    // Given:

    // When:
    mc().player().chat("/lua spell.owner.rotationYaw = 180.1; print('ok')");

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
  }

}
