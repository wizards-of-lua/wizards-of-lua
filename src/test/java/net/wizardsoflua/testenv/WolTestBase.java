package net.wizardsoflua.testenv;

import org.junit.After;
import org.junit.Before;

import com.google.common.collect.Iterables;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

public class WolTestBase extends TestDataFactory {
  private MinecraftBackdoor mcBackdoor;

  @Before
  public void before() {
    WolTestEnvironment testEnv = WolTestEnvironment.instance;
    testEnv.beforeTest();
    mcBackdoor = new MinecraftBackdoor(testEnv, MinecraftForge.EVENT_BUS);
  }
  
  @After
  public void after() {
    WolTestEnvironment testEnv = WolTestEnvironment.instance;
    testEnv.afterTest();
  }

  protected MinecraftBackdoor mc() {
    return mcBackdoor;
  }

  protected Iterable<String> messagesOf(Iterable<ServerChatEvent> events) {
    return Iterables.transform(events, ServerChatEvent::getMessage);
  }

  protected Iterable<BlockPos> positionsOf(Iterable<RightClickBlock> events) {
    return Iterables.transform(events, RightClickBlock::getPos);
  }

}
