package net.wizardsoflua.testenv;

import org.junit.Before;

import com.google.common.collect.Iterables;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

public class WolTestBase extends TestDataFactory {
  private MinecraftServer server;
  private MinecraftServerBackdoor serverBackdoor;

  @Before
  public void before() {
    server = WolTestEnvironment.instance.getServer();
    WolTestEnvironment.instance.clearEvents();
  }

  protected MinecraftServerBackdoor server() {
    if (serverBackdoor == null) {
      serverBackdoor = new MinecraftServerBackdoor(server, MinecraftForge.EVENT_BUS,
          WolTestEnvironment.instance);
    }
    return serverBackdoor;
  }

  protected Iterable<String> messagesOf(Iterable<ServerChatEvent> events) {
    return Iterables.transform(events, ServerChatEvent::getMessage);
  }
  
  protected Iterable<BlockPos> positionsOf(Iterable<RightClickBlock> events) {
    return Iterables.transform(events, RightClickBlock::getPos);
  }

  protected EntityPlayerMP player() {
    // TODO support also other dimensions
    WorldServer world = server.worldServerForDimension(1);
    return FakePlayerFactory.getMinecraft(world);
  }
}
