package net.wizardsoflua.lua.classes.entity;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.AbstractFunction3;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyCachingLuaClass;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.nbt.NbtConverter;

@DeclareLuaClass(name = EntityClass.NAME)
public class EntityClass extends ProxyCachingLuaClass<Entity, EntityClass.Proxy<?>> {
  public static final String NAME = "Entity";

  public EntityClass() {
    add("move", new MoveFunction());
    add("putNbt", new PutNbtFunction());
    add("addTag", new AddTagFunction());
    add("removeTag", new RemoveTagFunction());
    add("scanView", new ScanViewFunction());
    add("dropItem", new DropItemFunction());
    add("kill", new KillFunction());
  }

  @Override
  public Proxy<?> toLua(Entity delegate) {
    if (delegate instanceof EntityLivingBase) {
      return new EntityLivingBaseProxy<>(getConverters(), getMetaTable(),
          (EntityLivingBase) delegate);
    }
    return new Proxy<>(getConverters(), getMetaTable(), delegate);
  }

  public static class Proxy<D extends Entity> extends DelegatingProxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addReadOnly("dimension", () -> delegate.dimension);
      addReadOnly("uuid", this::getUuid);
      addReadOnly("alive", this::isAlive);
      add("name", this::getName, this::setName);
      add("pos", this::getPos, this::setPos);
      addReadOnly("nbt", this::getNbt);

      addReadOnly("facing", this::getFacing);
      add("lookVec", this::getLookVector, this::setLookVector);
      add("rotationYaw", this::getRotationYaw, this::setRotationYaw);
      add("rotationPitch", this::getRotationPitch, this::setRotationPitch);
      addReadOnly("eyeHeight", () -> delegate.getEyeHeight());
      add("motion", this::getMotion, this::setMotion);
      add("tags", this::getTags, this::setTags);

      // addReadOnly("world", this::getWorld);
    }

    @Override
    public boolean isTransferable() {
      return true;
    }

    public Object isAlive() {
      return getConverters().toLua(!delegate.isDead);
    }

    public Object getPos() {
      return getConverters().toLua(delegate.getPositionVector());
    }

    public void setPos(Object luaObj) {
      Vec3d pos = getConverters().toJava(Vec3d.class, luaObj);
      delegate.setPositionAndUpdate(pos.xCoord, pos.yCoord, pos.zCoord);
    }

    public Object getFacing() {
      EnumFacing result = delegate.getHorizontalFacing();
      return getConverters().toLua(result);
    }

    public @Nullable Object getLookVector() {
      Vec3d result = delegate.getLookVec();
      return getConverters().toLuaNullable(result);
    }

    public void setLookVector(Object luaObj) {
      Vec3d vec = getConverters().toJava(Vec3d.class, luaObj);
      double pitch = Math.toDegrees(Math.asin(-vec.yCoord));
      double yaw = Math.toDegrees(MathHelper.atan2(-vec.xCoord, vec.zCoord));
      setRotationYawAndPitch((float) yaw, (float) pitch);
    }

    public float getRotationYaw() {
      return MathHelper.wrapDegrees(delegate.rotationYaw);
    }

    /**
     * Sets the rotation yaw (rot-y) value.
     *
     * @param luaObj
     * @see Entity#readFromNBT(NBTTagCompound)
     */
    public void setRotationYaw(Object luaObj) {
      float yaw = getConverters().toJava(Number.class, luaObj).floatValue();
      delegate.setRotationYawHead(yaw);
      delegate.setRenderYawOffset(yaw);
      delegate.setPositionAndRotation(delegate.posX, delegate.posY, delegate.posZ, yaw,
          delegate.rotationPitch);
    }

    public double getRotationPitch() {
      return delegate.rotationPitch;
    }

    public void setRotationPitch(Object luaObj) {
      float pitch = getConverters().toJava(Number.class, luaObj).floatValue();
      delegate.setPositionAndRotation(delegate.posX, delegate.posY, delegate.posZ,
          delegate.rotationYaw, pitch);
    }

    public void setRotationYawAndPitch(float yaw, float pitch) {
      delegate.setRotationYawHead(yaw);
      delegate.setRenderYawOffset(yaw);
      delegate.setPositionAndRotation(delegate.posX, delegate.posY, delegate.posZ, yaw, pitch);
    }

    public Object getMotion() {
      double x = delegate.motionX;
      double y = delegate.motionY;
      double z = delegate.motionZ;
      Object result = getConverters().toLua(new Vec3d(x, y, z));
      return result;
    }

    public void setMotion(Object luaObj) {
      Vec3d v = getConverters().toJava(Vec3d.class, luaObj);
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
      return getConverters().toLuaIterable(result);
    }

    public void setTags(Object luaObj) {
      Collection<String> tags = getConverters().toJavaCollection(String.class, luaObj, "tags");

      for (String oldTag : Lists.newArrayList(delegate.getTags())) {
        delegate.removeTag(oldTag);
      }
      for (String newTag : tags) {
        delegate.addTag(newTag);
      }
    }

    public Object getUuid() {
      return getConverters().toLua(delegate.getUniqueID().toString());
    }

    public Object getName() {
      return getConverters().toLua(delegate.getName());
    }

    public void setName(Object luaObj) {
      String name = getConverters().toJava(String.class, luaObj);
      delegate.setCustomNameTag(name);
    }

    public Table getNbt() {
      NBTTagCompound nbt = new NBTTagCompound();
      delegate.writeToNBT(nbt);
      Table result = NbtConverter.toLua(nbt);
      return result;
    }

    public void putNbt(Object luaObj) {
      Table nbtTable = getConverters().castToTable(luaObj);
      // UUID origUuid = delegate.getUniqueID();
      NBTTagCompound oldNbt = new NBTTagCompound();
      delegate.writeToNBT(oldNbt);
      NBTTagCompound newNbt = converters.getNbtConverter().merge(oldNbt, nbtTable);
      // TODO (mk) we don't need to merge them again, do we?
      // oldNbt.merge(newNbt);
      delegate.readFromNBT(newNbt);
      // delegate.setUniqueId(origUuid);
    }

    public void move(String directionName, @Nullable Number distance) {
      Direction direction = Direction.byName(directionName);
      checkNotNull(direction != null, "expected direction but got %s", direction);
      if (distance == null) {
        distance = 1L;
      }
      Vec3d vec = direction.getDirectionVec(getRotationYaw());
      double x = delegate.posX + vec.xCoord * distance.doubleValue();
      double y = delegate.posY + vec.yCoord * distance.doubleValue();
      double z = delegate.posZ + vec.zCoord * distance.doubleValue();
      delegate.setPositionAndUpdate(x, y, z);
    }

    public boolean addTag(Object luaObj) {
      String tag = getConverters().toJava(String.class, luaObj);
      return delegate.addTag(tag);
    }

    public boolean removeTag(Object luaObj) {
      String tag = getConverters().toJava(String.class, luaObj);
      return delegate.removeTag(tag);
    }

    public Object scanView(Object luaObj) {
      float distance = getConverters().toJavaOld(Number.class, luaObj, "distance").floatValue();
      Vec3d start = delegate.getPositionEyes(0);
      Vec3d end = start.add(delegate.getLookVec().scale(distance));
      RayTraceResult hit = delegate.getEntityWorld().rayTraceBlocks(start, end, false);
      Object result = getConverters().toLuaNullable(hit);
      return result;
    }

    public Object dropItem(ItemStack item, @Nullable Float offsetY) {
      if (item.getCount() == 0) {
        throw new IllegalArgumentException("Can't drop an item with count==0");
      }
      offsetY = ofNullable(offsetY).orElse(0f);
      EntityItem result = delegate.entityDropItem(item, offsetY);
      return getConverters().toLuaNullable(result);
    }

    public void kill() {
      delegate.onKillCommand();
    }

    // public Object getWorld() {
    // World world = delegate.getEntityWorld();
    // return getConverters().toLua(world);
    // }

  }

  public static class EntityLivingBaseProxy<D extends EntityLivingBase> extends Proxy<D> {
    public EntityLivingBaseProxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      add("mainhand", this::getMainhand, this::setMainhand);
      add("offhand", this::getOffhand, this::setOffhand);
    }

    @Override
    public float getRotationYaw() {
      float v = delegate.renderYawOffset;
      return MathHelper.wrapDegrees(v);
    }

    public @Nullable Object getMainhand() {
      ItemStack itemStack = delegate.getHeldItemMainhand();
      if (itemStack.isEmpty()) {
        return null;
      }
      return getConverters().toLuaNullable(itemStack);
    }

    public void setMainhand(@Nullable Object luaObj) {
      ItemStack itemStack = getConverters().toJavaNullable(ItemStack.class, luaObj);
      if (itemStack == null) {
        itemStack = ItemStack.EMPTY;
      }
      delegate.setHeldItem(EnumHand.MAIN_HAND, itemStack);
    }

    public @Nullable Object getOffhand() {
      ItemStack itemStack = delegate.getHeldItemOffhand();
      if (itemStack.isEmpty()) {
        return null;
      }
      return getConverters().toLuaNullable(itemStack);
    }

    public void setOffhand(@Nullable Object luaObj) {
      ItemStack itemStack = getConverters().toJavaNullable(ItemStack.class, luaObj);
      if (itemStack == null) {
        itemStack = ItemStack.EMPTY;
      }
      delegate.setHeldItem(EnumHand.OFF_HAND, itemStack);
    }


  }

  private class MoveFunction extends AbstractFunction3 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2, Object arg3)
        throws ResolvedControlThrowable {
      Proxy<?> proxy = castToProxy(arg1);
      String direction = getConverters().toJava(String.class, arg2);
      Number distance = getConverters().toJavaNullable(Number.class, arg3);
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
      Proxy<?> proxy = castToProxy(arg1);
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
      Proxy<?> proxy = castToProxy(arg1);
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
      Proxy<?> proxy = castToProxy(arg1);
      boolean result = proxy.removeTag(arg2);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class ScanViewFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2) {
      Proxy<?> proxy = castToProxy(arg1);
      Object result = proxy.scanView(arg2);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class DropItemFunction extends AbstractFunction3 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2, Object arg3) {
      Proxy<?> proxy = castToProxy(arg1);
      String functionName = "dropItem";
      ItemStack item = getConverters().toJava(ItemStack.class, arg2, 2, "item", functionName);
      Float offsetY = getConverters().toJavaNullable(Float.class, arg3, 3, "offsetY", functionName);
      Object result = proxy.dropItem(item, offsetY);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class KillFunction extends AbstractFunction1 {
    @Override
    public void invoke(ExecutionContext context, Object arg1) {
      Proxy<?> proxy = castToProxy(arg1);
      proxy.kill();
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
}
