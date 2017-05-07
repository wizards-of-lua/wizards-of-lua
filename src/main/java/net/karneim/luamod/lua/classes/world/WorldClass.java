package net.karneim.luamod.lua.classes.world;

import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.world.World;
import net.sandius.rembulan.Table;

public class WorldClass extends DelegatingLuaClass<World> {
  public WorldClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends World> b, World delegate) {
    b.addReadOnly("difficulty", () -> repo.wrap(delegate.getDifficulty()));
    b.addReadOnly("name", () -> repo.wrap(delegate.getWorldInfo().getWorldName()));
    b.addReadOnly("seed", () -> repo.wrap(delegate.getSeed()));
    b.addReadOnly("spawnPoint", () -> repo.wrap(delegate.getSpawnPoint()));
    b.addReadOnly("worldTime", () -> repo.wrap(delegate.getWorldTime()));
    b.addReadOnly("worldType", () -> repo.wrap(delegate.getWorldType().getWorldTypeName()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
