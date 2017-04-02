package net.karneim.luamod.lua.classes;

import net.karneim.luamod.Entities;
import net.karneim.luamod.lua.NBTTagUtil;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.table.Entry;
import net.karneim.luamod.lua.util.table.TableIterable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.karneim.luamod.lua.wrapper.EntityInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.karneim.luamod.lua.wrapper.StringIterableInstance;
import net.karneim.luamod.lua.wrapper.WrapperFactory;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.AbstractFunction0;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

@TypeName("Entity")
@ModulePackage(Constants.MODULE_PACKAGE)
public class EntityClass extends AbstractLuaType {

  private final Entities entities;

  public EntityClass(Entities entities) {
    this.entities = entities;
  }

  public void installInto(ChunkLoader loader, DirectCallExecutor executor, StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc = loader.loadTextChunk(new Variable(getRepo().getEnv()), getTypeName(),
        String.format("require \"%s\"", getModule()));
    executor.call(state, classFunc);
    addFunctions(getRepo().getEnv());
  }

  public EntityInstance<Entity> newInstance(Entity delegate) {
    return new EntityInstance<Entity>(getRepo(), delegate,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  private void addFunctions(Table env) {
    Table metatable = Metatables.get(getRepo().getEnv(), getTypeName());
    metatable.rawset("addTag", new AddTagFunction());
    metatable.rawset("removeTag", new RemoveTagFunction());
    metatable.rawset("setTags", new SetTagsFunction());
    metatable.rawset("getData", new GetDataFunction());
  }

  private class AddTagFunction extends AbstractFunction2 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("table expected but got nil!"));
      }
      if (arg2 == null) {
        throw new IllegalArgumentException(String.format("string expected but got nil!"));
      }
      Entity delegate = DelegatingTableWrapper.getDelegate(Entity.class, arg1);

      String tag = String.valueOf(arg2);

      if (delegate.getTags().contains(tag)) {
        context.getReturnBuffer().setTo(false);
      } else {
        delegate.addTag(tag);
        context.getReturnBuffer().setTo(true);
      }
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class RemoveTagFunction extends AbstractFunction2 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("table expected but got nil!"));
      }
      if (arg2 == null) {
        throw new IllegalArgumentException(String.format("string expected but got nil!"));
      }
      Entity delegate = DelegatingTableWrapper.getDelegate(Entity.class, arg1);
      String tag = String.valueOf(arg2);
      boolean changed = delegate.removeTag(tag);
      context.getReturnBuffer().setTo(changed);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class SetTagsFunction extends AbstractFunction2 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("table expected but got nil!"));
      }
      if (arg2 == null) {
        throw new IllegalArgumentException(String.format("table expected but got nil!"));
      }
      if (!(arg2 instanceof Table)) {
        throw new IllegalArgumentException(
            String.format("table expected but got %s", arg2.getClass().getSimpleName()));
      }
      Entity delegate = DelegatingTableWrapper.getDelegate(Entity.class, arg1);
      delegate.getTags().clear();
      for (Entry<Object, Object> entry : new TableIterable((Table) arg2)) {
        String tag = String.valueOf(entry.getValue());
        delegate.addTag(tag);
      }
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  /**
   * Returns the NBT-Data of the entity.
   */
  private class GetDataFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("table expected but got nil!"));
      }
      Entity delegate = DelegatingTableWrapper.getDelegate(Entity.class, arg1);
      NBTTagCompound tagCompound = delegate.writeToNBT(new NBTTagCompound());
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

  ///////////

  // Class Functions

  /**
   * Returns the IDs of all (loaded) entities.
   */
  private class IdsFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      Iterable<String> ids = entities.list();
      StringIterableInstance wrapper = StringIterableClass.get().newInstance(getRepo(), ids);
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
      DelegatingTable result = WrapperFactory.wrap(getRepo(), entity);
      context.getReturnBuffer().setTo(result);
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
      StringIterableInstance wrapper = StringIterableClass.get().newInstance(getRepo(), names);
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
