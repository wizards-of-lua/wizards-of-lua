package net.karneim.luamod.lua.classes.entity.item;

import static net.karneim.luamod.lua.util.LuaPreconditions.checkType;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.sandius.rembulan.Table;

@LuaModule("EntityMinecartFurnace")
public class EntityMinecartFurnaceClass extends DelegatingLuaClass<EntityMinecartFurnace> {
  public EntityMinecartFurnaceClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends EntityMinecartFurnace> b,
      EntityMinecartFurnace d) {
    b.add("pushX", () -> repo.wrap(d.pushX),
        o -> d.pushX = checkType(o, Number.class).doubleValue());
    b.add("pushZ", () -> repo.wrap(d.pushZ),
        o -> d.pushZ = checkType(o, Number.class).doubleValue());
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
