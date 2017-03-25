package net.karneim.luamod.lua.wrapper;

import static net.karneim.luamod.lua.util.PreconditionsUtils.checkType;
import static net.karneim.luamod.lua.wrapper.WrapperFactory.wrap;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.NBTTagUtil;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.table.Entry;
import net.karneim.luamod.lua.util.table.TableIterable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class EntityWrapper<E extends Entity> extends DelegatingTableWrapper<E> {

  private static final String CLASSNAME = "Entity";
  public static final String MODULE = "net.karneim.luamod.lua.classes." + CLASSNAME;

  public static LuaFunction NEW(Table env) {
    Table metatable = Metatables.get(env, CLASSNAME);
    LuaFunction result = (LuaFunction) metatable.rawget("new");
    return result;
  }

  public EntityWrapper(Table env, @Nullable E delegate) {
    super(env, delegate);
  }

  private void setPosition(Object object) {
    Table vector = checkType(object, Table.class);
    Number x = checkType(vector.rawget("x"), Number.class);
    Number y = checkType(vector.rawget("y"), Number.class);
    Number z = checkType(vector.rawget("z"), Number.class);
    delegate.setPositionAndUpdate(x.doubleValue(), y.doubleValue(), z.doubleValue());
  }

  private ByteString getTeam() {
    Team team = delegate.getTeam();
    return team != null ? ByteString.of(team.getRegisteredName()) : null;
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b) {
    b.add("id", delegate::getCachedUniqueIdString, null);
    b.add("name", delegate::getName,
        (Object name) -> delegate.setCustomNameTag(String.valueOf(name)));
    b.add("dimension", () -> delegate.dimension,
        (Object dimension) -> delegate.dimension = checkType(dimension, Number.class).intValue());
    b.add("pos", () -> wrap(env, delegate.getPositionVector()), this::setPosition);
    b.add("blockPos", () -> wrap(env, delegate.getPosition()), null);
    b.add("orientation", () -> wrap(env, delegate.getHorizontalFacing()), null);
    b.add("rotationYaw", () -> delegate.rotationYaw,
        (Object yaw) -> delegate.rotationYaw = checkType(yaw, Number.class).floatValue());
    b.add("rotationPitch", () -> delegate.rotationPitch,
        (Object pitch) -> delegate.rotationPitch = checkType(pitch, Number.class).floatValue());
    b.add("lookVec", () -> wrap(env, delegate.getLookVec()), null);
    b.add("team", this::getTeam, null);
    b.add("tags", () -> wrap(env, delegate.getTags()), null);

    Table metatable = Metatables.get(env, CLASSNAME);
    b.setMetatable(metatable);
  }
  
  public static void addFunctions(Table env) {
    Table metatable = Metatables.get(env, CLASSNAME);
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
      Entity delegate = getDelegate(Entity.class, arg1);

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
      Entity delegate = getDelegate(Entity.class, arg1);
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
      Entity delegate = getDelegate(Entity.class, arg1);
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
      Entity delegate = getDelegate(Entity.class, arg1);
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
