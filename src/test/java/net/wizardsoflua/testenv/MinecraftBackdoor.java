package net.wizardsoflua.testenv;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.wizardsoflua.TimeService;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;
import net.wizardsoflua.testenv.player.PlayerBackdoor;

public class MinecraftBackdoor {

  private final WolTestenv testenv;
  private final PlayerBackdoor player;

  public MinecraftBackdoor(WolTestenv testenv) {
    this.testenv = requireNonNull(testenv, "testenv");
    player = new PlayerBackdoor(this);
  }

  public WolTestenv getTestenv() {
    return testenv;
  }

  private EventRecorder getEventRecorder() {
    return testenv.getEventRecorder();
  }

  private EntityPlayerMP getTestPlayer() {
    return testenv.getTestPlayer();
  }

  private MinecraftServer getServer() {
    return testenv.getServer();
  }

  private WorldServer getOverworld() {
    return getServer().getWorld(DimensionType.OVERWORLD);
  }

  private WorldInfo getWorldInfo() {
    return getOverworld().getWorldInfo();
  }

  public String getWorldName() {
    return getWorldInfo().getWorldName();
  }

  public int getWorldDimension() {
    return DimensionType.OVERWORLD.getId();
  }

  public EnumDifficulty getDifficulty() {
    return getWorldInfo().getDifficulty();
  }

  public void setDifficulty(EnumDifficulty newDifficulty) {
    getWorldInfo().setDifficulty(newDifficulty);
  }

  public void post(Event event) {
    MinecraftForge.EVENT_BUS.post(event);
  }

  public PlayerBackdoor player() {
    return player;
  }

  public void clearEvents() {
    testenv.runOnMainThread(() -> getEventRecorder().clear());
  }

  public <E extends Event> E waitFor(Class<E> eventType) {
    try {
      testenv.waitForPendingActions();
      return getEventRecorder().waitFor(eventType, 5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public String nextClientMessage() {
    return waitFor(TestPlayerReceivedChatEvent.class).getMessage();
  }

  public String nextServerMessage() {
    return waitFor(ServerLog4jEvent.class).getMessage();
  }

  public void executeCommand(String format, Object... args) {
    String command;
    if (args != null && args.length > 0) {
      command = String.format(format, args);
    } else {
      command = format;
    }
    testenv.runChangeOnMainThread(() -> {
      MinecraftServer server = getServer();
      CommandSource source = server.getCommandSource();
      server.getCommandManager().handleCommand(source, command);
    });
  }

  public void freezeClock(long millis) {
    ZoneId zoneId = ZoneId.systemDefault();
    Clock clock = Clock.fixed(Instant.ofEpochMilli(millis), zoneId);
    testenv.getTimeService().setClock(clock);
  }

  public void freezeClock(LocalDateTime date) {
    ZoneId zoneId = ZoneId.systemDefault();
    Clock clock = Clock.fixed(date.atZone(zoneId).toInstant(), zoneId);
    testenv.getTimeService().setClock(clock);
  }

  public void resetClock() {
    TimeService timeService = testenv.getTimeService();
    timeService.setClock(timeService.getDefaultClock());
  }

  public void breakAllSpells() {
    testenv.runOnMainThread(() -> {
      Collection<SpellEntity> spells = testenv.getSpellRegistry().getAll();
      for (SpellEntity spell : spells) {
        spell.setDead();
      }
    });
  }

  public Iterable<SpellEntity> spells() {
    return testenv.getSpellRegistry().getAll();
  }

  public void setBlock(BlockPos pos, Block blockType) {
    World world = getTestPlayer().getEntityWorld();
    testenv.runChangeOnMainThread(() -> world.setBlockState(pos, blockType.getDefaultState()));
  }

  public IBlockState getBlock(BlockPos pos) {
    return testenv.callOnMainThread(() -> getOverworld().getBlockState(pos));
  }

  public void setChest(BlockPos pos, ItemStack itemStack) {
    testenv.runChangeOnMainThread(() -> {
      World world = getTestPlayer().getEntityWorld();
      world.setBlockState(pos, Blocks.CHEST.getDefaultState());
      TileEntityChest chest = (TileEntityChest) world.getTileEntity(pos);
      chest.setInventorySlotContents(0, itemStack);
    });
  }

  public BlockPos getWorldSpawnPoint() {
    return getOverworld().getSpawnPoint();
  }

  public void setWorldSpawnPoint(BlockPos pos) {
    getOverworld().setSpawnPoint(pos);
  }

  public BlockPos getNearestVillageCenter(BlockPos pos, int radius) {
    Village v = getOverworld().getVillageCollection().getNearestVillage(pos, radius);
    if (v == null) {
      return null;
    }
    return v.getCenter();
  }

  public int getLuaTicksLimit() {
    return testenv.getConfig().getGeneralConfig().getLuaTicksLimit();
  }

  public void setLuaTicksLimit(int luaTicksLimit) {
    testenv.runOnMainThread(
        () -> testenv.getConfig().getGeneralConfig().setLuaTicksLimit(luaTicksLimit));
  }

  public int getEventListenerLuaTicksLimit() {
    return testenv.getConfig().getGeneralConfig().getEventListenerLuaTicksLimit();
  }

  public void setEventListenerLuaTicksLimit(int eventListenerluaTicksLimit) {
    testenv.runOnMainThread(() -> testenv.getConfig().getGeneralConfig()
        .setEventListenerLuaTicksLimit(eventListenerluaTicksLimit));
  }

  public @Nullable List<? extends Entity> findEntities(String target) {
    try {
      CommandSource source = getServer().getCommandSource();
      EntitySelectorParser parser = new EntitySelectorParser(new StringReader(target));
      EntitySelector selector = parser.parse();
      return selector.select(source);
    } catch (CommandSyntaxException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public void createTeam(String team) {
    getServer().getWorldScoreboard().createTeam(team);
  }

  public void deleteTeams() {
    Scoreboard scoreboard = getServer().getWorldScoreboard();
    for (ScorePlayerTeam team : new ArrayList<>(scoreboard.getTeams())) {
      scoreboard.removeTeam(team);
    }
  }

  public void clearWizardConfigs() throws IOException {
    testenv.getConfig().clearWizardConfigs();
  }

  public void createSharedModule(String moduleName, String content) {
    File moduleFile = getSharedModuleFile(moduleName);
    if (moduleFile.exists()) {
      moduleFile.delete();
    }
    moduleFile.getParentFile().mkdirs();
    try {
      Files.asCharSink(moduleFile, Charsets.UTF_8).write(content);
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
    return new File(testenv.getConfig().getSharedLibDir(), path);
  }

  public void deleteSharedModule(String moduleName) {
    File file = getSharedModuleFile(moduleName);
    file.delete();
  }

  public void writeWorldFile(String path, String content) {
    FileSystem fs = testenv.getWorldFileSystem();
    Path p = fs.getPath(path);
    try {
      Files.createParentDirs(p.toFile());
      Files.asCharSink(p.toFile(), Charsets.UTF_8).write(content);
    } catch (IOException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public String readWorldFile(String path) {
    FileSystem fs = testenv.getWorldFileSystem();
    Path p = fs.getPath(path);
    try {
      if (java.nio.file.Files.exists(p)) {
        return Files.asByteSource(p.toFile()).asCharSource(Charsets.UTF_8).read();
      } else {
        return null;
      }
    } catch (IOException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public void deleteWorldFile(String path) {
    try {
      FileSystem fs = testenv.getWorldFileSystem();
      delete(fs.getPath(path));
    } catch (IOException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  private void delete(Path path) throws IOException {
    if (java.nio.file.Files.isDirectory(path)) {
      for (Path child : java.nio.file.Files.list(path).collect(Collectors.toList())) {
        delete(child);
      }
    }
    java.nio.file.Files.deleteIfExists(path);
  }

  public boolean existsWorldFile(String path) {
    FileSystem fs = testenv.getWorldFileSystem();
    Path p = fs.getPath(path);
    return java.nio.file.Files.exists(p);
  }

  public void bar(BlockPos startPos, EnumFacing direction, int meter, Block blockType) {
    checkArgument(meter >= 1, "meter must be greater than or equal to 1, but was %s!", meter);
    for (int i = 0; i < meter; ++i) {
      BlockPos pos = startPos.offset(direction, i);
      setBlock(pos, blockType);
    }
  }

  public void setGameTime(long gameTime) {
    getWorldInfo().setWorldTotalTime(gameTime);
  }

  public long getGameTime() {
    return getWorldInfo().getGameTime();
  }

  private @Nullable GameRuleDsl gameRules;

  public GameRuleDsl gameRules() {
    if (gameRules == null) {
      gameRules = new GameRuleDsl(getServer());
    }
    return gameRules;
  }
}
