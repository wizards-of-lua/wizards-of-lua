package net.wizardsoflua.testenv;

import java.lang.reflect.UndeclaredThrowableException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.testenv.player.PlayerBackdoor;

public class MinecraftBackdoor {

  private final WolTestEnvironment testEnv;
  private final EventBus eventBus;
  private final PlayerBackdoor player;

  public MinecraftBackdoor(WolTestEnvironment testEnv, EventBus eventBus) {
    this.testEnv = testEnv;
    this.eventBus = eventBus;
    this.player = new PlayerBackdoor(testEnv);
  }

  public String getName() {
    return testEnv.getServer().getName();
  }

  public void post(Event event) {
    eventBus.post(event);
  }

  public PlayerBackdoor player() {
    return player;
  }

  public <E extends Event> E waitFor(Class<E> eventType) {
    try {
      return testEnv.getEventRecorder().waitFor(eventType, 5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public void executeCommand(String format, Object... args) {
    String command;
    if (args != null && args.length > 0) {
      command = String.format(format, args);
    } else {
      command = format;
    }
    ICommandSender sender = testEnv.getServer();
    MinecraftServer server = testEnv.getServer();
    // server.addScheduledTask(new Runnable() {
    // @Override
    // public void run() {
    // server.getCommandManager().executeCommand(sender, command);
    // }
    // });
    testEnv.runAndWait(() -> server.getCommandManager().executeCommand(sender, command));
  }

  public void freezeClock(long millis) {
    ZoneId zoneId = ZoneId.systemDefault();
    Clock clock = Clock.fixed(Instant.ofEpochMilli(millis), zoneId);
    testEnv.getWol().setClock(clock);
  }

  public void freezeClock(LocalDateTime date) {
    ZoneId zoneId = ZoneId.systemDefault();
    Clock clock = Clock.fixed(date.atZone(zoneId).toInstant(), zoneId);
    testEnv.getWol().setClock(clock);
  }

  public void resetClock() {
    Clock clock = testEnv.getWol().getDefaultClock();
    testEnv.getWol().setClock(clock);
  }

  public void clearEvents() {
    testEnv.runAndWait(()->testEnv.getEventRecorder().clear());
  }
  
  public void breakAllSpells() {
    testEnv.runAndWait(() -> testEnv.getWol().getSpellRegistry().breakAll());
  }

  public Iterable<SpellEntity> spells() {
    return testEnv.getWol().getSpellRegistry().getAll();
  }

  public void setBlock(BlockPos pos, Block blockType) {
    World world = testEnv.getTestPlayer().getEntityWorld();
    world.setBlockState(pos, blockType.getDefaultState());
  }

  public IBlockState getBlock(BlockPos pos) {
    World world = testEnv.getTestPlayer().getEntityWorld();
    return world.getBlockState(pos);
  }

  public BlockPos getWorldSpawnPoint() {
    return testEnv.getServer().getEntityWorld().getSpawnPoint();
  }

  public void setLuaTicksLimit(int luaTicksLimit) {
    testEnv.runAndWait(() -> testEnv.getWol().getConfig().setLuaTicksLimit(luaTicksLimit));
  }

  public int getLuaTicksLimit() {
    return testEnv.getWol().getConfig().getLuaTicksLimit();
  }
  
}
