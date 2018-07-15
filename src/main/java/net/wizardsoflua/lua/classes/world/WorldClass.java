package net.wizardsoflua.lua.classes.world;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import com.google.auto.service.AutoService;
import net.minecraft.util.math.BlockPos;
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
@GenerateLuaDoc(subtitle = "Where We All Exist")
public class WorldClass extends BasicLuaClass<World, WorldClass.Instance<World>> {
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
      return delegate.provider.getDimension();
    }

    /**
     * This is the name of this world.
     */
    @LuaProperty
    public String getName() {
      return delegate.getWorldInfo().getWorldName();
    }

    /**
     * The spawn point is a certain point in this world where [Players](/omdule/Player) will spawn
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
     * The difficulty defines how difficult it is for the player to live in this world.
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
     * The 'canSeeSky' function returns true if the sky is visible from the given position when
     * looking straight up.
     *
     * @param pos
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
  }
}
