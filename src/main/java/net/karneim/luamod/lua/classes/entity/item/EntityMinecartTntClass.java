package net.karneim.luamod.lua.classes.entity.item;

import static net.karneim.luamod.lua.util.LuaPreconditions.checkTypeDelegatingTable;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

@LuaModule("EntityMinecartTnt")
public class EntityMinecartTntClass extends DelegatingLuaClass<EntityMinecartTNT> {
  public EntityMinecartTntClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends EntityMinecartTNT> b, EntityMinecartTNT d) {
    b.addReadOnly("fuseTicks", () -> repo.wrap(d.getFuseTicks()));
    b.addReadOnly("ignited", () -> repo.wrap(d.isIgnited()));
  }

  @Override
  protected void addFunctions(Table luaClass) {
    luaClass.rawset("ignite", new IgniteFunction());
  }

  private final class IgniteFunction extends AbstractFunction1 {
    @Override
    public void invoke(ExecutionContext context, Object arg0) throws ResolvedControlThrowable {
      EntityMinecartTNT delegate = checkTypeDelegatingTable(arg0, EntityMinecartTNT.class);

      delegate.ignite();

      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
}
