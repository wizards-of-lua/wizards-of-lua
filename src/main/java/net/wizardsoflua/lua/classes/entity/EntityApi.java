package net.wizardsoflua.lua.classes.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaModule;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.classes.ObjectClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;
import net.wizardsoflua.scribble.LuaApiBase;

@LuaModule(name = EntityApi.NAME, superClass = ObjectClass.class)
public class EntityApi<D extends Entity> extends LuaApiBase<D> {
  public static final String NAME = "Entity";

  public EntityApi(ProxyingLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }

  @LuaProperty
  public int getDimension() {
    return delegate.dimension;
  }

  @LuaProperty
  public float getEyeHeight() {
    return delegate.getEyeHeight();
  }

  @LuaProperty
  public boolean isAlive() {
    return !delegate.isDead;
  }

  @LuaProperty
  public Vec3d getPos() {
    return delegate.getPositionVector();
  }

  @LuaProperty
  public void setPos(Vec3d pos) {
    delegate.setPositionAndUpdate(pos.xCoord, pos.yCoord, pos.zCoord);
  }

  @LuaProperty
  public EnumFacing getFacing() {
    return delegate.getHorizontalFacing();
  }

  @LuaProperty
  public @Nullable Vec3d getLookVec() {
    return delegate.getLookVec();
  }

  @LuaProperty
  public void setLookVec(Vec3d lookVec) {
    double pitch = Math.toDegrees(Math.asin(-lookVec.yCoord));
    double yaw = Math.toDegrees(MathHelper.atan2(-lookVec.xCoord, lookVec.zCoord));
    setRotationYawAndPitch((float) yaw, (float) pitch);
  }

  protected void setRotationYawAndPitch(float yaw, float pitch) {
    delegate.setRotationYawHead(yaw);
    delegate.setRenderYawOffset(yaw);
    delegate.setPositionAndRotation(delegate.posX, delegate.posY, delegate.posZ, yaw, pitch);
  }

  @LuaProperty
  public float getRotationYaw() {
    return MathHelper.wrapDegrees(delegate.rotationYaw);
  }

  /**
   * Sets the rotation yaw (rot-y) value.
   *
   * @param luaObj
   * @see Entity#readFromNBT(NBTTagCompound)
   */
  @LuaProperty
  public void setRotationYaw(float rotationYaw) {
    delegate.setRotationYawHead(rotationYaw);
    delegate.setRenderYawOffset(rotationYaw);
    delegate.setPositionAndRotation(delegate.posX, delegate.posY, delegate.posZ, rotationYaw,
        delegate.rotationPitch);
  }

  @LuaProperty
  public float getRotationPitch() {
    return delegate.rotationPitch;
  }

  @LuaProperty
  public void setRotationPitch(float rotationPitch) {
    delegate.setPositionAndRotation(delegate.posX, delegate.posY, delegate.posZ,
        delegate.rotationYaw, rotationPitch);
  }

  @LuaProperty
  public Vec3d getMotion() {
    double x = delegate.motionX;
    double y = delegate.motionY;
    double z = delegate.motionZ;
    return new Vec3d(x, y, z);
  }

  @LuaProperty
  public void setMotion(Vec3d motion) {
    double x = motion.xCoord;
    double y = motion.yCoord;
    double z = motion.zCoord;

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

  @LuaProperty(type = Collection.class)
  public Collection<String> getTags() {
    return delegate.getTags();
  }

  @LuaProperty(type = Collection.class)
  public void setTags(Object luaObj) {
    Collection<String> tags = getConverters().toJavaCollection(String.class, luaObj, "tags");

    for (String oldTag : Lists.newArrayList(delegate.getTags())) {
      delegate.removeTag(oldTag);
    }
    for (String newTag : tags) {
      delegate.addTag(newTag);
    }
  }

  @LuaProperty
  public String getUuid() {
    return delegate.getUniqueID().toString();
  }

  @LuaProperty
  public String getName() {
    return delegate.getName();
  }

  @LuaProperty
  public void setName(String name) {
    delegate.setCustomNameTag(name);
  }

  @LuaProperty
  public NBTTagCompound getNbt() {
    NBTTagCompound nbt = new NBTTagCompound();
    delegate.writeToNBT(nbt);
    return nbt;
  }

  @LuaFunction
  public void putNbt(Table nbt) {
    NBTTagCompound oldNbt = delegate.serializeNBT();
    NBTTagCompound newNbt = getConverters().getNbtConverter().merge(oldNbt, nbt);
    delegate.readFromNBT(newNbt);
  }

  @LuaFunction
  public void move(String directionName, @Nullable Double distance) {
    Direction direction = Direction.byName(directionName);
    checkNotNull(direction != null, "expected direction but got %s", direction);
    if (distance == null) {
      distance = 1d;
    }
    Vec3d vec = direction.getDirectionVec(getRotationYaw());
    double x = delegate.posX + vec.xCoord * distance;
    double y = delegate.posY + vec.yCoord * distance;
    double z = delegate.posZ + vec.zCoord * distance;
    delegate.setPositionAndUpdate(x, y, z);
  }

  @LuaFunction
  public boolean addTag(String tag) {
    return delegate.addTag(tag);
  }

  @LuaFunction
  public boolean removeTag(String tag) {
    return delegate.removeTag(tag);
  }

  @LuaFunction
  public RayTraceResult scanView(float distance) {
    Vec3d start = delegate.getPositionEyes(0);
    Vec3d end = start.add(delegate.getLookVec().scale(distance));
    return delegate.getEntityWorld().rayTraceBlocks(start, end, false);
  }

  @LuaFunction
  public EntityItem dropItem(ItemStack item, float offsetY) {
    if (item.getCount() == 0) {
      throw new IllegalArgumentException("Can't drop an item with count==0");
    }
    return delegate.entityDropItem(item, offsetY);
  }

  @LuaFunction
  public void kill() {
    delegate.onKillCommand();
  }
}
