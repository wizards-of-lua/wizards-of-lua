package net.karneim.luamod.lua.classes.event.entity.living;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.karneim.luamod.lua.wrapper.UnmodifiableIterableWrapper;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.sandius.rembulan.Table;

@LuaModule("LivingDropsEvent")
public class LivingDropsEventClass extends DelegatingLuaClass<LivingDropsEvent> {
  public LivingDropsEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends LivingDropsEvent> b,
      LivingDropsEvent delegate) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
    b.addReadOnly("source", () -> repo.wrap(delegate.getSource()));
    UnmodifiableIterableWrapper<EntityItem, ?> wrapper =
        new UnmodifiableIterableWrapper<>(entityItem -> repo.wrap(entityItem));
    b.addReadOnly("drops", () -> wrapper.createLuaObject(delegate.getDrops()));
    b.addReadOnly("lootingLevel", () -> repo.wrap(delegate.getLootingLevel()));
    b.addReadOnly("recentlyHit", () -> repo.wrap(delegate.isRecentlyHit()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
