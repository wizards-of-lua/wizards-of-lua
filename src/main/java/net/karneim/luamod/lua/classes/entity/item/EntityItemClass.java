package net.karneim.luamod.lua.classes.entity.item;

import static net.karneim.luamod.lua.util.LuaPreconditions.checkTypeDelegatingTableNullable;
import static net.karneim.luamod.lua.util.LuaPreconditions.checkTypeInt;
import static net.karneim.luamod.lua.util.LuaPreconditions.checkTypeStringNullable;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.sandius.rembulan.Table;

@LuaModule("EntityItem")
public class EntityItemClass extends DelegatingLuaClass<EntityItem> {
  public EntityItemClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends EntityItem> b, EntityItem delegate) {
    b.add("lifespan", () -> delegate.lifespan, o -> delegate.lifespan = checkTypeInt(o));
    b.addReadOnly("lifespan", delegate::cannotPickup);
    b.add("item", () -> repo.wrap(delegate.getEntityItem()),
        o -> delegate.setEntityItemStack(checkTypeDelegatingTableNullable(o, ItemStack.class)));
    b.add("owner", delegate::getOwner, o -> delegate.setOwner(checkTypeStringNullable(o)));
    b.add("thrower", delegate::getThrower, o -> delegate.setThrower(checkTypeStringNullable(o)));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
