package net.karneim.luamod.lua.wrapper;

import net.karneim.luamod.Entities;
import net.karneim.luamod.lua.DynamicTable;
import net.karneim.luamod.lua.NBTTagUtil;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction0;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class EntitiesWrapper {

  public static EntitiesWrapper installInto(Table env, Entities entities) {
    EntitiesWrapper result = new EntitiesWrapper(entities);
    env.rawset("entities", result.getLuaTable());
    return result;
  }

  private final Entities entities;
  private final Table luaTable = DefaultTable.factory().newTable();

  private final EntityWrapperFactory entityWrapperFactory = new EntityWrapperFactory();

  public EntitiesWrapper(Entities entities) {
    this.entities = entities;
    luaTable.rawset("list", new ListFunction());
    luaTable.rawset("get", new GetFunction());
    luaTable.rawset("find", new FindFunction());
    luaTable.rawset("put", new PutFunction());
    luaTable.rawset("getData", new GetDataFunction());
  }

  public Table getLuaTable() {
    return luaTable;
  }

  private class ListFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      Iterable<String> names = entities.list();
      StringIterableWrapper wrapper = new StringIterableWrapper(names);
      context.getReturnBuffer().setTo(wrapper.getLuaObject());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class GetFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("Entity ID expected but got nil!"));
      }
      String name = String.valueOf(arg1);
      Entity entity = entities.get(name);
      EntityWrapper<?> wrapper = entityWrapperFactory.create(entity);
      context.getReturnBuffer().setTo(wrapper.getLuaObject());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class GetDataFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("Entity ID expected but got nil!"));
      }
      String name = String.valueOf(arg1);
      Entity entity = entities.get(name);

      NBTTagCompound tagCompound = entity.writeToNBT(new NBTTagCompound());
      DynamicTable.Builder builder = new DynamicTable.Builder(null);
      if ( tagCompound != null) {
        NBTTagUtil.insertValues(builder, tagCompound);
      }
      DynamicTable tbl = builder.build();

      context.getReturnBuffer().setTo(tbl);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class FindFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("@ expression expected but got nil!"));
      }
      String target = String.valueOf(arg1);
      Iterable<String> names = entities.find(target);
      StringIterableWrapper wrapper = new StringIterableWrapper(names);
      context.getReturnBuffer().setTo(wrapper.getLuaObject());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class PutFunction extends AbstractFunction2 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("Arg 1: Entity ID expected but got nil!"));
      }
      if (arg2 == null) {
        throw new IllegalArgumentException(String.format("Arg 2: Table expected but got nil!"));
      }
      if (!(arg2 instanceof Table)) {
        throw new IllegalArgumentException(
            String.format("Arg 2: Table expected but got %s", arg2.getClass().getSimpleName()));
      }
      String name = String.valueOf(arg1);
      entities.put(name, ((Table) arg2));
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

}
