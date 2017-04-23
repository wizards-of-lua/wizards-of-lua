package net.karneim.luamod.lua.classes;

import java.util.UUID;

import net.karneim.luamod.lua.nbt.NBTTagUtil;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.table.Entry;
import net.karneim.luamod.lua.util.table.TableIterable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.karneim.luamod.lua.wrapper.EntityInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

@LuaClass("Entity")
public class EntityClass extends AbstractLuaType {
  @Override
  protected void addFunctions() {}

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
    metatable.rawset("putData", new PutDataFunction());
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

  /**
   * Put the NBT-Data into the entity.
   */
  private class PutDataFunction extends AbstractFunction2 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("table expected but got nil!"));
      }
      Entity delegate = DelegatingTableWrapper.getDelegate(Entity.class, arg1);
      if (arg2 == null) {
        throw new IllegalArgumentException(String.format("table expected but got nil!"));
      }
      if (!(arg2 instanceof Table)) {
        throw new IllegalArgumentException(String.format("table expected but got %s", arg2));
      }
      Table data = (Table) arg2;
      UUID uuid = delegate.getUniqueID();
      NBTTagCompound origTag = delegate.writeToNBT(new NBTTagCompound());
      NBTTagCompound mergedTag = NBTTagUtil.merge(origTag, data);
      delegate.readFromNBT(mergedTag);
      delegate.setUniqueId(uuid);
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }



}
