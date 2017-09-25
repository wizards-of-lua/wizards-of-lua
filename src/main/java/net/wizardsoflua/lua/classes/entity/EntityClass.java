package net.wizardsoflua.lua.classes.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.AbstractFunction3;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.classes.vec3.Vec3Class;
import net.wizardsoflua.lua.module.types.Terms;
import net.wizardsoflua.lua.nbt.NbtConverter;

public class EntityClass {
  public static final String METATABLE_NAME = "Entity";

  private final Converters converters;
  private final Table metatable;

  public EntityClass(Converters converters) {
    this.converters = converters;
    // TODO do declaration outside this class
    this.metatable = converters.getTypes().declare(METATABLE_NAME);
    metatable.rawset("move", new MoveFunction());
    metatable.rawset("putNbt", new PutNbtFunction());
    metatable.rawset("addTag", new AddTagFunction());
    metatable.rawset("removeTag", new RemoveTagFunction());
  }

  public Table toLua(Entity delegate) {
    return new Proxy(converters, metatable, delegate);
  }

  public static class Proxy extends DelegatingProxy {

    private final Entity delegate;

    public Proxy(Converters converters, Table metatable, Entity delegate) {
      super(converters, metatable, delegate);
      this.delegate = delegate;
      addReadOnly("dimension", () -> delegate.dimension);
      addReadOnly("uuid", this::getUuid);
      add("name", this::getName, this::setName);
      add("pos", this::getPos, this::setPos);
      addReadOnly("nbt", this::getNbt);

      addReadOnly("orientation", this::getOrientation);
      addReadOnly("lookVec", this::getLookVector);
      add("rotationYaw", this::getRotationYaw, this::setRotationYaw);
      add("rotationPitch", this::getRotationPitch, this::setRotationPitch);
      addReadOnly("eyeHeight", () -> delegate.getEyeHeight());
      add("motion", this::getMotion, this::setMotion);
      add("tags", this::getTags, this::setTags);
    }

    public Table getPos() {
      return getConverters().vec3ToLua(delegate.getPositionVector());
    }

    public void setPos(Object luaObj) {
      Vec3d pos = getConverters().vec3ToJava(luaObj);
      delegate.setPositionAndUpdate(pos.xCoord, pos.yCoord, pos.zCoord);
    }

    public ByteString getOrientation() {
      EnumFacing result = delegate.getHorizontalFacing();
      return getConverters().enumToLua(result);
    }

    public Table getLookVector() {
      Vec3d result = delegate.getLookVec();
      return getConverters().vec3ToLua(result);
    }

    public double getRotationYaw() {
      return MathHelper.wrapDegrees(delegate.rotationYaw);
    }

    /**
     * Sets the rotation yaw (rot-y) value.
     * 
     * @param luaObj
     * @see Entity#readFromNBT(NBTTagCompound)
     */
    public void setRotationYaw(Object luaObj) {
      float yaw = getConverters().getTypes().castNumber(luaObj, Terms.MANDATORY).floatValue();
      delegate.setRotationYawHead(yaw);
      delegate.setRenderYawOffset(yaw);
      delegate.setPositionAndRotation(delegate.posX, delegate.posY, delegate.posZ, yaw,
          delegate.rotationPitch);
    }

    public double getRotationPitch() {
      return delegate.rotationPitch;
    }

    public void setRotationPitch(Object luaObj) {
      Number pitch = getConverters().getTypes().castNumber(luaObj, Terms.MANDATORY);
      delegate.setPositionAndRotation(delegate.posX, delegate.posY, delegate.posZ,
          delegate.rotationYaw, pitch.floatValue());
    }

    public Table getMotion() {
      double x = delegate.motionX;
      double y = delegate.motionY;
      double z = delegate.motionZ;
      return getConverters().vec3ToLua(new Vec3d(x, y, z));
    }

    public void setMotion(Object luaObj) {
      getConverters().getTypes().checkAssignable(Vec3Class.METATABLE_NAME, luaObj, Terms.MANDATORY);
      Vec3d v = getConverters().vec3ToJava(luaObj);
      double x = v.xCoord;
      double y = v.yCoord;
      double z = v.zCoord;

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

    public Table getTags() {
      Set<String> result = delegate.getTags();
      return getConverters().stringsToLua(result);
    }

    public void setTags(Object luaObj) {
      Iterable<String> tags = getConverters().stringsToJava(luaObj, Terms.MANDATORY);

      for (String oldTag : Lists.newArrayList(delegate.getTags())) {
        delegate.removeTag(oldTag);
      }
      for (String newTag : tags) {
        delegate.addTag(newTag);
      }
    }

    public ByteString getUuid() {
      return getConverters().stringToLua(delegate.getUniqueID().toString());
    }

    public ByteString getName() {
      return getConverters().stringToLua(delegate.getName());
    }

    public void setName(Object luaObj) {
      String name = getConverters().stringToJava(luaObj);
      delegate.setCustomNameTag(name);
    }

    public Table getNbt() {
      NBTTagCompound nbt = new NBTTagCompound();
      delegate.writeToNBT(nbt);
      Table result = NbtConverter.toLua(nbt);
      return result;
    }

    public void putNbt(Object luaObj) {
      Table nbtTable = getConverters().getTypes().castTable(luaObj, Terms.MANDATORY);
      UUID origUuid = delegate.getUniqueID();
      NBTTagCompound oldNbt = new NBTTagCompound();
      delegate.writeToNBT(oldNbt);
      NBTTagCompound newNbt = NbtConverter.merge(oldNbt, nbtTable);
      // TODO (mk) we don't need to merge them again, do we?
      // oldNbt.merge(newNbt);
      delegate.readFromNBT(newNbt);
      // delegate.setUniqueId(origUuid);
    }

    public void move(String direction, @Nullable Number distance) {
      EnumFacing facing = EnumFacing.byName(direction);
      checkNotNull(facing != null, "expected direction but got %s", direction);
      if (distance == null) {
        distance = 1L;
      }
      Vec3i vec = facing.getDirectionVec();
      double x = delegate.posX + vec.getX() * distance.doubleValue();
      double y = delegate.posY + vec.getY() * distance.doubleValue();
      double z = delegate.posZ + vec.getZ() * distance.doubleValue();
      delegate.setPositionAndUpdate(x, y, z);
    }

    public boolean addTag(Object luaObj) {
      String tag = getConverters().getTypes().castString(luaObj, Terms.MANDATORY);
      return delegate.addTag(tag);
    }

    public boolean removeTag(Object luaObj) {
      String tag = getConverters().getTypes().castString(luaObj, Terms.MANDATORY);
      return delegate.removeTag(tag);
    }

  }

  private class MoveFunction extends AbstractFunction3 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2, Object arg3)
        throws ResolvedControlThrowable {
      converters.getTypes().checkAssignable(METATABLE_NAME, arg1, Terms.MANDATORY);
      Proxy proxy = (Proxy) arg1;
      String direction = converters.getTypes().castString(arg2, Terms.MANDATORY);
      Number distance = converters.getTypes().castNumber(arg3, Terms.OPTIONAL);
      proxy.move(direction, distance);
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class PutNbtFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2) {
      converters.getTypes().checkAssignable(METATABLE_NAME, arg1, Terms.MANDATORY);
      Proxy proxy = (Proxy) arg1;
      proxy.putNbt(arg2);
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class AddTagFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2) {
      converters.getTypes().checkAssignable(METATABLE_NAME, arg1, Terms.MANDATORY);
      Proxy proxy = (Proxy) arg1;
      boolean result = proxy.addTag(arg2);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class RemoveTagFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2) {
      converters.getTypes().checkAssignable(METATABLE_NAME, arg1, Terms.MANDATORY);
      Proxy proxy = (Proxy) arg1;
      boolean result = proxy.removeTag(arg2);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

}
