package net.wizardsoflua.tests;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;
import net.wizardsoflua.testenv.server.ServerProxy;

@RunWith(MinecraftJUnitRunner.class)
public class TestEnvironmentTest extends WolTestBase {

  // /test net.wizardsoflua.tests.TestEnvironmentTest test_can_receive_ServerChatEvent
  @Test
  public void test_can_receive_ServerChatEvent() {
    // Given:
    String message = "hello";

    // When:
    mc().post(newServerChatEvent(mc().player().getDelegate(), message));

    // Then:
    ServerChatEvent act = mc().waitFor(ServerChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(message);
  }

  // /test net.wizardsoflua.tests.TestEnvironmentTest test_can_receive_RightClickBlock_Event
  @Test
  public void test_can_receive_RightClickBlock_Event() {
    // Given:
    BlockPos pos = BlockPos.ORIGIN;

    // When:
    mc().post(newRightClickBlockEvent(mc().player().getDelegate(), pos));

    // Then:
    RightClickBlock act = mc().waitFor(RightClickBlock.class);
    assertThat(act.getPos()).isEqualTo(pos);
  }

  // /test net.wizardsoflua.tests.TestEnvironmentTest test_can_receive_console_output
  @Test
  public void test_can_receive_console_output() {
    // Given:
    String message = "hello";

    // When:
    mc().player().getDelegate().sendMessage(new TextComponentString(message));

    // Then:
    TestPlayerReceivedChatEvent act = mc().waitFor(TestPlayerReceivedChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(message);
  }

  // /test net.wizardsoflua.tests.TestEnvironmentTest test_can_move_player_around
  @Test
  public void test_can_move_player_around() {
    // Given:
    BlockPos pos = new BlockPos(1, 4, 1);

    // When:
    mc().player().setPosition(pos);

    // Then
    BlockPos act = mc().player().getDelegate().getPosition();
    assertThat(act).isEqualTo(pos);
  }

  // /test net.wizardsoflua.tests.TestEnvironmentTest test_player_can_post_chat_message
  @Test
  public void test_player_can_post_chat_message() {
    // Given:
    String message = "hello";

    // When:
    mc().player().chat(message);

    // Then:
    ServerChatEvent act = mc().waitFor(ServerChatEvent.class);
    assertThat(act.getMessage()).isEqualTo(message);
  }

  // /test net.wizardsoflua.tests.TestEnvironmentTest test_player_can_post_several_chat_messages
  @Test
  public void test_player_can_post_several_chat_messages() {
    // Given:
    String message1 = "hello";
    String message2 = "dude";

    // When:
    mc().player().chat(message1);
    mc().player().chat(message2);

    // Then:
    ServerChatEvent act1 = mc().waitFor(ServerChatEvent.class);
    assertThat(act1.getMessage()).isEqualTo(message1);
    ServerChatEvent act2 = mc().waitFor(ServerChatEvent.class);
    assertThat(act2.getMessage()).isEqualTo(message2);
  }

  // /test net.wizardsoflua.tests.TestEnvironmentTest test_player_can_leftclick_on_blockpos
  @Test
  public void test_player_can_leftclick_on_blockpos() {
    // Given:
    BlockPos playerPos = new BlockPos(0, 4, -1);
    mc().player().setPosition(playerPos);
    mc().player().setRotationYaw(0);
    BlockPos clickPos = new BlockPos(0, 3, 0);
    EnumFacing facing = EnumFacing.UP;

    // When:
    mc().player().leftclick(clickPos, facing);

    // Then:
    LeftClickBlock act = mc().waitFor(LeftClickBlock.class);
    assertThat(act.getPos()).isEqualTo(clickPos);
    assertThat(act.getFace()).isEqualTo(facing);
  }

  // /test net.wizardsoflua.tests.TestEnvironmentTest test_player_can_rightclick_on_blockpos
  @Test
  public void test_player_can_rightclick_on_blockpos() {
    // Given:
    BlockPos playerPos = new BlockPos(0, 4, -1);
    mc().player().setPosition(playerPos);
    mc().player().setRotationYaw(0);
    BlockPos clickPos = new BlockPos(0, 3, 0);
    EnumFacing facing = EnumFacing.UP;
    Vec3d hitvec = new Vec3d(clickPos);
    EnumHand hand = EnumHand.MAIN_HAND;

    // When:
    mc().player().rightclick(clickPos, facing, hitvec, hand);

    // Then:
    RightClickBlock act = mc().waitFor(RightClickBlock.class);
    assertThat(act.getPos()).isEqualTo(clickPos);
    assertThat(act.getFace()).isEqualTo(facing);
  }

  // /test net.wizardsoflua.tests.TestEnvironmentTest test_waitFor_has_timeout
  // @Test
  // public void test_waitFor_has_timeout() {
  // // Given:
  //
  // // When:
  // Exception act = null;
  // try {
  // mc().waitFor(LeftClickBlock.class);
  // } catch (Exception e) {
  // act = e;
  // }
  //
  // // Then:
  // assertThat(act).isExactlyInstanceOf(RuntimeException.class);
  // }

  // /test net.wizardsoflua.tests.TestEnvironmentTest test_can_receive_log4j_event
  @Test
  public void test_can_receive_log4j_event() {
    // Given:
    Logger logger = LogManager.getLogger(ServerProxy.NET_MINECRAFT_LOGGER);
    String message = "hello";

    // When:
    logger.info(message);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(message);
  }

  // /test net.wizardsoflua.tests.TestEnvironmentTest test_can_set_block
  @Test
  public void test_can_set_block() {
    // Given:
    BlockPos pos = new BlockPos(1, 4, 1);
    mc().setBlock(pos, Blocks.AIR);

    // When:
    mc().setBlock(pos, Blocks.DIAMOND_BLOCK);

    // Then:
    assertThat(mc().getBlock(pos)).isA(Blocks.DIAMOND_BLOCK);
  }

  @After
  public void clearBlock() {
    BlockPos pos = new BlockPos(1, 4, 1);
    mc().setBlock(pos, Blocks.AIR);
  }
}
