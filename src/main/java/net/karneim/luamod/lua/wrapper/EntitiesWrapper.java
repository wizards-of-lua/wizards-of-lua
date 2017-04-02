package net.karneim.luamod.lua.wrapper;

import com.google.common.base.Preconditions;

import net.karneim.luamod.Entities;
import net.karneim.luamod.lua.NBTTagUtil;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.classes.StringIterableClass;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.table.DelegatingTable;
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

  public static EntitiesWrapper installInto(LuaTypesRepo repo, Entities entities) {
    EntitiesWrapper result = new EntitiesWrapper(repo, entities);
    repo.getEnv().rawset("entities", result.getLuaTable());
    return result;
  }

  private final LuaTypesRepo repo;
  private final Entities entities;
  private final Table luaTable = DefaultTable.factory().newTable();

  public EntitiesWrapper(LuaTypesRepo repo, Entities entities) {
    this.repo = Preconditions.checkNotNull(repo);
    this.entities = entities;
    luaTable.rawset("list", new ListFunction());
    luaTable.rawset("get", new GetFunction());
    luaTable.rawset("find", new FindFunction());
    // luaTable.rawset("put", new PutFunction()); // not supported so far
    luaTable.rawset("getData", new GetDataFunction());
  }

  public Table getLuaTable() {
    return luaTable;
  }

  /**
   * Returns the IDs of all (loaded) entities.
   */
  private class ListFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      Iterable<String> ids = entities.list();
      StringIterableInstance wrapper = StringIterableClass.get().newInstance(repo, ids);
      context.getReturnBuffer().setTo(wrapper.getLuaObject());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  /**
   * Returns the entity with the given ID.
   */
  private class GetFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("Entity ID expected but got nil!"));
      }
      String name = String.valueOf(arg1);
      Entity entity = entities.get(name);
      DelegatingTable result = WrapperFactory.wrap(repo, entity);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  /**
   * Returns the NBT-Data of the entity with the given ID.
   */
  private class GetDataFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("Entity ID expected but got nil!"));
      }
      String id = String.valueOf(arg1);
      Entity entity = entities.get(id);

      NBTTagCompound tagCompound = entity.writeToNBT(new NBTTagCompound());
      PatchedImmutableTable.Builder builder = new PatchedImmutableTable.Builder();
      if (tagCompound != null) {
        NBTTagUtil.insertValues(builder, tagCompound);
      }
      PatchedImmutableTable tbl = builder.build();

      context.getReturnBuffer().setTo(tbl);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  /**
   * Returns the IDs of all (loaded) entities matching the given selector.
   */
  private class FindFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("@ expression expected but got nil!"));
      }
      String target = String.valueOf(arg1);
      Iterable<String> names = entities.find(target);
      StringIterableInstance wrapper = StringIterableClass.get().newInstance(repo, names);
      context.getReturnBuffer().setTo(wrapper.getLuaObject());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  /**
   * Modifies the entity with the given ID by merging the given NBT-Data-Table into it.
   */
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
