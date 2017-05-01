package net.karneim.luamod.lua.classes.event.entity.player;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.sandius.rembulan.Table;

@LuaModule("AchievementEvent")
public class AchievementEventClass extends DelegatingLuaClass<AchievementEvent> {
  public AchievementEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends AchievementEvent> b, AchievementEvent delegate) {
    b.addReadOnly("achievement", () -> repo.wrap(delegate.getAchievement()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
