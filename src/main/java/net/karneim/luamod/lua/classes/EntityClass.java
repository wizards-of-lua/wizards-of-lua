package net.karneim.luamod.lua.classes;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.karneim.luamod.lua.util.PreconditionsUtils.checkType;
import static net.karneim.luamod.lua.util.PreconditionsUtils.checkTypeString;

import java.util.UUID;

import net.karneim.luamod.lua.nbt.NBTTagUtil;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.table.Entry;
import net.karneim.luamod.lua.util.table.TableIterable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

@LuaModule("Entity")
public class EntityClass extends DelegatingLuaClass<Entity> {
  public EntityClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends Entity> b, Entity delegate) {
    EntityWrapper d = new EntityWrapper(delegate);
    b.add("id", delegate::getCachedUniqueIdString, null);
    b.add("name", delegate::getName,
        (Object name) -> delegate.setCustomNameTag(String.valueOf(name)));
    b.add("dimension", () -> delegate.dimension,
        (Object dimension) -> delegate.dimension = checkType(dimension, Number.class).intValue());
    b.add("pos", () -> repo.wrap(delegate.getPositionVector()), d::setPosition);
    b.add("blockPos", () -> repo.wrap(new Vec3d(delegate.getPosition())), null);
    b.add("eyeHeight", () -> delegate.getEyeHeight(), null);
    b.add("orientation", () -> repo.wrap(delegate.getHorizontalFacing()), null);
    b.add("rotationYaw", () -> MathHelper.wrapDegrees(delegate.rotationYaw), d::setRotationYaw);
    b.add("rotationPitch", () -> delegate.rotationPitch, d::setRotationPitch);
    b.add("lookVec", () -> repo.wrap(delegate.getLookVec()), null);
    b.add("team", d::getTeam, null);
    b.add("tags", () -> repo.wrapStrings(delegate.getTags()), null);
    b.add("facing", () -> repo.wrap(delegate.getAdjustedHorizontalFacing()), null);
    b.add("motion",
        () -> repo.wrap(new Vec3d(delegate.motionX, delegate.motionY, delegate.motionZ)),
        d::setMotion);
  }

  private static class EntityWrapper {
    private final Entity delegate;

    public EntityWrapper(Entity delegate) {
      this.delegate = checkNotNull(delegate, "delegate == null!");
    }

    private void setRotationYaw(Object object) {
      float yaw = checkType(object, Number.class).floatValue();
      delegate.setPositionAndRotation(delegate.posX, delegate.posY, delegate.posZ, yaw,
          delegate.rotationPitch);
      if (delegate instanceof EntityPlayerMP) {
        ((EntityPlayerMP) delegate).connection.setPlayerLocation(delegate.posX, delegate.posY,
            delegate.posZ, delegate.rotationYaw, delegate.rotationPitch);
      }
    }

    private void setRotationPitch(Object object) {
      float pitch = checkType(object, Number.class).floatValue();
      delegate.setPositionAndRotation(delegate.posX, delegate.posY, delegate.posZ,
          delegate.rotationPitch, pitch);
      if (delegate instanceof EntityPlayerMP) {
        ((EntityPlayerMP) delegate).connection.setPlayerLocation(delegate.posX, delegate.posY,
            delegate.posZ, delegate.rotationYaw, delegate.rotationPitch);
      }
    }

    private void setMotion(Object object) {
      Table vector = checkType(object, Table.class);
      double x = checkType(vector.rawget("x"), Number.class).doubleValue();
      double y = checkType(vector.rawget("y"), Number.class).doubleValue();
      double z = checkType(vector.rawget("z"), Number.class).doubleValue();

      // see SPacketEntityVelocity
      double maxLen = 3.9;
      double lenSqr = x * x + y * y + z * z;
      if (lenSqr > maxLen * maxLen) {
        double f = maxLen / Math.sqrt(lenSqr);
        x = x * f;
        y = y * f;
        z = z * f;
      }

      delegate.motionX = x;
      delegate.motionY = y;
      delegate.motionZ = z;
      delegate.velocityChanged = true;
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
  }

  @Override
  protected void addFunctions(Table luaClass) {
    luaClass.rawset("addTag", new AddTagFunction());
    luaClass.rawset("removeTag", new RemoveTagFunction());
    luaClass.rawset("setTags", new SetTagsFunction());
    luaClass.rawset("getNbt", new GetNbtFunction());
    luaClass.rawset("putNbt", new PutNbtFunction());
  }

  private class AddTagFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      DelegatingTable<?> self = checkType(0, arg1, DelegatingTable.class);
      Entity delegate = checkType(0, self.getDelegate(), Entity.class);
      String tag = checkTypeString(1, arg2);
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
      DelegatingTable<?> self = checkType(0, arg1, DelegatingTable.class);
      Entity delegate = checkType(0, self.getDelegate(), Entity.class);
      String tag = checkTypeString(1, arg2);
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
      DelegatingTable<?> self = checkType(0, arg1, DelegatingTable.class);
      Entity delegate = checkType(0, self.getDelegate(), Entity.class);
      Table tags = checkType(1, arg2, Table.class);
      delegate.getTags().clear();
      for (Entry<Object, Object> entry : new TableIterable(tags)) {
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
  private class GetNbtFunction extends AbstractFunction1 {
    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      DelegatingTable<?> self = checkType(arg1, DelegatingTable.class);
      Entity delegate = checkType(self.getDelegate(), Entity.class);
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
  private class PutNbtFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      DelegatingTable<?> self = checkType(0, arg1, DelegatingTable.class);
      Entity delegate = checkType(0, self.getDelegate(), Entity.class);
      Table data = checkType(1, arg2, Table.class);
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
