package net.wizardsoflua.lua.classes.world;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.classes.LuaClassApi;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

/**
 * The World is the space around every creature and item in it.
 */
@GenerateLuaClass(name = WorldApi.NAME)
@GenerateLuaDoc(subtitle = "Where We All Exist")
public class WorldApi<D extends World> extends LuaClassApi<D> {
  public static final String NAME = "World";

  public WorldApi(ProxyingLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
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
   * The 'canSeeSky' function return true if the sky is visible from the given position when looking
   * straight up. 
   * 
   * @param pos
   */
  @LuaFunction
  public boolean canSeeSky(Vec3d pos) {
    return delegate.canSeeSky(new BlockPos(pos));
  }

}
