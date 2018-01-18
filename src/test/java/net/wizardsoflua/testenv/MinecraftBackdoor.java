package net.wizardsoflua.testenv;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
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

  public String getWorldName() {
    return testEnv.getServer().getEntityWorld().getWorldInfo().getWorldName();
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
    testEnv.runAndWait(() -> testEnv.getEventRecorder().clear());
  }

  public void breakAllSpells() {
    testEnv.runAndWait(() -> testEnv.getWol().getSpellRegistry().breakAll());
  }

  public Iterable<SpellEntity> spells() {
    return testEnv.getWol().getSpellRegistry().getAll();
  }

  public void setBlock(BlockPos pos, Block blockType) {
    World world = testEnv.getTestPlayer().getEntityWorld();
    testEnv.runAndWait(() -> world.setBlockState(pos, blockType.getDefaultState()));
  }

  public IBlockState getBlock(BlockPos pos) {
    World world = testEnv.getTestPlayer().getEntityWorld();
    return world.getBlockState(pos);
  }

  public void setChest(BlockPos pos, ItemStack itemStack) {
    World world = testEnv.getTestPlayer().getEntityWorld();
    testEnv.runAndWait(() -> {
      world.setBlockState(pos, Blocks.CHEST.getDefaultState());
      TileEntityChest chest = (TileEntityChest) world.getTileEntity(pos);
      chest.setInventorySlotContents(0, itemStack);
    });
  }

  public BlockPos getWorldSpawnPoint() {
    return testEnv.getServer().getEntityWorld().getSpawnPoint();
  }

  public void setWorldSpawnPoint(BlockPos pos) {
    testEnv.getServer().getEntityWorld().setSpawnPoint(pos);
  }

  public void setLuaTicksLimit(int luaTicksLimit) {
    testEnv.runAndWait(
        () -> testEnv.getWol().getConfig().getGeneralConfig().setLuaTicksLimit(luaTicksLimit));
  }

  public int getLuaTicksLimit() {
    return testEnv.getWol().getConfig().getGeneralConfig().getLuaTicksLimit();
  }

  public @Nullable List<Entity> findEntities(String target) {
    try {
      ICommandSender sender = testEnv.getServer();
      List<Entity> result = EntitySelector.<Entity>matchEntities(sender, target, Entity.class);
      return result;
    } catch (CommandException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public void createTeam(String team) {
    testEnv.getTestPlayer().getWorldScoreboard().createTeam(team);
  }

  public void deleteTeams() {
    Scoreboard scoreBoard = testEnv.getTestPlayer().getWorldScoreboard();
    for (ScorePlayerTeam team : Lists.newArrayList(scoreBoard.getTeams())) {
      scoreBoard.removeTeam(team);
    }
  }

  public void clearWizardConfigs() throws IOException {
    testEnv.getWol().getConfig().clearWizardConfigs();
  }

  public void createSharedModule(String moduleName, String content) {
    File moduleFile = getSharedModuleFile(moduleName);
    if (moduleFile.exists()) {
      moduleFile.delete();
    }
    moduleFile.getParentFile().mkdirs();
    try {
      Files.write(content, moduleFile, Charsets.UTF_8);
    } catch (IOException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public void deleteModule(String moduleName) {
    File moduleFile = getSharedModuleFile(moduleName);
    if (moduleFile.exists()) {
      moduleFile.delete();
    }
  }

  private File getSharedModuleFile(String moduleName) {
    String path = moduleName.replace(".", File.separator) + ".lua";
    return new File(testEnv.getWol().getConfig().getSharedLibDir(), path);
  }

  public void deleteSharedModule(String moduleName) {
    File file = getSharedModuleFile(moduleName);
    file.delete();
  }

  public ItemStack getItemStack(Block block) {
    IBlockState state = block.getDefaultState();
    RayTraceResult target = null; // unused
    World world = player().getDelegate().getEntityWorld();
    BlockPos pos = null; // unused
    return block.getPickBlock(state, target, world, pos, player().getDelegate());
  }

  public ItemStack getItemStack(Item item) {
    return new ItemStack(item);
  }

  public void clearLuaFunctionCache() {
    testEnv.getWol().clearLuaFunctionCache();
  }

  public void bar(BlockPos startPos, EnumFacing direction, int meter, Block blockType) {
    checkArgument(meter >= 1, "meter must be greater than or equal to 1, but was %s!", meter);
    for (int i = 0; i < meter; ++i) {
      BlockPos pos = startPos.offset(direction, i);
      setBlock(pos, blockType);
    }
  }


}
