package net.wizardsoflua.lua.classes.world;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import com.google.auto.service.AutoService;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.Village;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.LuaInstance;
import net.wizardsoflua.lua.classes.common.Delegator;

/**
 * The World is the space around every creature and item in it.
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = WorldClass.NAME)
@GenerateLuaClassTable(instance = WorldClass.Instance.class)
@GenerateLuaDoc(subtitle = "Where All Exists")
public final class WorldClass extends BasicLuaClass<World, WorldClass.Instance<World>> {
  public static final String NAME = "World";
  @Resource
  private LuaConverters converters;

  @Override
  protected Table createRawTable() {
    return new WorldClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<World>> toLuaInstance(World javaInstance) {
    return new WorldClassInstanceTable<>(new Instance<>(javaInstance), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends World> extends LuaInstance<D> {

    public Instance(D delegate) {
      super(delegate);
    }

    /**
     * The 'dimension' is a magic number that defines which kind of world this is. 0 means the
     * Overworld. -1 is the Nether, and 1 is the End.
     */
    @LuaProperty
    public int getDimension() {
      return delegate.getDimension().getType().getId();
    }

    /**
     * This is the name of this world.
     */
    @LuaProperty
    public String getName() {
      return delegate.getWorldInfo().getWorldName();
    }

    /**
     * The spawn point is a certain point in this world where [Players](/module/Player) will spawn
     * when they enter the world the first time, or when their personal spawn point is somehow not
     * accessible anymore.
     */
    @LuaProperty
    public Vec3d getSpawnPoint() {
      return new Vec3d(delegate.getSpawnPoint());
    }

    @LuaProperty
    public void setSpawnPoint(Vec3d value) {
      delegate.setSpawnPoint(new BlockPos(value));
    }

    /**
     * The 'daytime' is the number of game ticks that have passed since the last sunrise. In
     * Mincraft the day runs from sunrise to sunrise and is divided into 24000 game ticks. For
     * example, 0 means sunrise, 6000 noon, 12000 sunset, 18000 midnight, and 23999 is the end of
     * the night.
     */
    @LuaProperty
    public long getDaytime() {
      return delegate.getWorldTime() % 24000;
    }

    /**
     * The difficulty defines how difficult it is for the players to live in this world. This is one
     * of PEACEFUL, EASY, NORMAL, HARD.
     */
    @LuaProperty
    public EnumDifficulty getDifficulty() {
      return delegate.getWorldInfo().getDifficulty();
    }

    @LuaProperty
    public void setDifficulty(EnumDifficulty value) {
      delegate.getWorldInfo().setDifficulty(checkNotNull(value, "value==null!"));
    }

    /**
     * The 'time' is the number of game ticks that have passed since the world has been created. But
     * in contrast to [Time.gametime](/modules/Time#gametime) this value can be modified by
     * operators using the <tt>/time</tt> command.
     */
    @LuaProperty
    public long getTime() {
      return delegate.getWorldTime();
    }

    @LuaProperty
    public void setTime(long time) {
      delegate.setWorldTime(time);
    }

    /**
     * The 'canSeeSky' function returns true if the sky is visible from the given position when
     * looking straight up.
     *
     */
    @LuaFunction
    public boolean canSeeSky(Vec3d pos) {
      return delegate.canSeeSky(new BlockPos(pos));
    }

    /**
     * The 'getNearestVillage' function returns the village center of the nearest
     * [village](https://minecraft.gamepedia.com/Tutorials/Village_mechanics) relative to the given
     * position, or nil, if no village is found within the given radius.
     */
    @LuaFunction
    public @Nullable Vec3d getNearestVillage(Vec3d position, int radius) {
      Village v = delegate.getVillageCollection().getNearestVillage(new BlockPos(position), radius);
      if (v == null) {
        return null;
      }
      return new Vec3d(v.getCenter());
    }

    /**
     * The 'isLoadedAt' function returns true if the world chunk that contains the given world
     * coordinates is currently loaded into the server.
     */
    @LuaFunction
    public boolean isLoadedAt(Vec3d pos) {
      return delegate.isBlockLoaded(new BlockPos(pos));
    }

    /**
     * The 'isGeneratedAt' function returns true if the world chunk that contains the given world
     * coordinates has already been generated. Please note that this doesn't imply that this chunk
     * also is currently loaded.
     */
    @LuaFunction
    public boolean isGeneratedAt(Vec3d pos) {
      int chunkX = MathHelper.floor(pos.x) >> 4;
      int chunkZ = MathHelper.floor(pos.z) >> 4;
      return delegate.getChunkProvider().isChunkGeneratedAt(chunkX, chunkZ);
    }

  }
}
