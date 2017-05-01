package net.karneim.luamod.lua.classes.stats;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.stats.Achievement;
import net.sandius.rembulan.Table;

@LuaModule("Achievement")
public class AchievementClass extends DelegatingLuaClass<Achievement> {
  public AchievementClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends Achievement> b, Achievement d) {
    b.addReadOnly("parentAchievement", () -> repo.wrap(d.parentAchievement));
    b.addReadOnly("isSpecial", () -> repo.wrap(d.getSpecial()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
