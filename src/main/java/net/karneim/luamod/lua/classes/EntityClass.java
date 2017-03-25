package net.karneim.luamod.lua.classes;

import static net.karneim.luamod.lua.util.PreconditionsUtils.checkType;
import static net.karneim.luamod.lua.wrapper.WrapperFactory.wrap;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.NBTTagUtil;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.table.Entry;
import net.karneim.luamod.lua.util.table.TableIterable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.karneim.luamod.lua.wrapper.EntityInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class EntityClass {

  private final String classname = "Entity";
  public final String module = "net.karneim.luamod.lua.classes." + classname;

  private static final EntityClass SINGLETON = new EntityClass();
  
  public static EntityClass get() {
    return SINGLETON;
  }
  
  public void installInto(Table env, ChunkLoader loader, DirectCallExecutor executor,
      StateContext state) throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc =
        loader.loadTextChunk(new Variable(env), classname, String.format("require \"%s\"", module));
    executor.call(state, classFunc);
    addFunctions(env);
  }
  
  public EntityInstance<Entity> newInstance(Table env, Entity delegate) {
    return new EntityInstance<Entity>(env, delegate, Metatables.get(env, classname));
  }
  
  private void addFunctions(Table env) {
    Table metatable = Metatables.get(env, classname);
    metatable.rawset("addTag", new AddTagFunction());
    metatable.rawset("removeTag", new RemoveTagFunction());
    metatable.rawset("setTags", new SetTagsFunction());
    metatable.rawset("getData", new GetDataFunction());
  }

  private static class AddTagFunction extends AbstractFunction2 {

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

  private static class RemoveTagFunction extends AbstractFunction2 {

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

  private static class SetTagsFunction extends AbstractFunction2 {

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
  private static class GetDataFunction extends AbstractFunction1 {

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

}
