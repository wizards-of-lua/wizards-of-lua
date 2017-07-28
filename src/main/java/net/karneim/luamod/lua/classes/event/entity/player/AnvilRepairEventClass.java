package net.karneim.luamod.lua.classes.event.entity.player;

import static net.karneim.luamod.lua.util.LuaPreconditions.checkType;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.sandius.rembulan.Table;

@LuaModule("AnvilRepairEvent")
public class AnvilRepairEventClass extends DelegatingLuaClass<AnvilRepairEvent> {
  public AnvilRepairEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends AnvilRepairEvent> b, AnvilRepairEvent d) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
    b.add("breakChance", () -> repo.wrap(d.getBreakChance()),
        o -> d.setBreakChance(checkType(o, Number.class).floatValue()));
    b.addReadOnly("left", () -> repo.wrap(d.getLeft()));
    b.addReadOnly("output", () -> repo.wrap(d.getOutput()));
    b.addReadOnly("right", () -> repo.wrap(d.getRight()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
