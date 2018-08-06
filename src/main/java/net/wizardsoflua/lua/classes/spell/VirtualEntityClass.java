package net.wizardsoflua.lua.classes.spell;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import com.google.auto.service.AutoService;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.LuaInstance;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.classes.entity.Direction;
import net.wizardsoflua.spell.VirtualEntity;

/**
 * The VirtualEntity class is the base class of all server-only entities that populate the world.
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = VirtualEntityClass.NAME)
@GenerateLuaClassTable(instance = VirtualEntityClass.Instance.class)
@GenerateLuaDoc(subtitle = "The Base Class of all Virtual Entities")
public final class VirtualEntityClass
    extends BasicLuaClass<VirtualEntity, VirtualEntityClass.Instance<VirtualEntity>> {
  public static final String NAME = "VirtualEntity";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new VirtualEntityClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<VirtualEntity>> toLuaInstance(VirtualEntity javaInstance) {
    return new VirtualEntityClassInstanceTable<>(new Instance<>(javaInstance, injector), getTable(),
        converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends VirtualEntity> extends LuaInstance<D> {
    @Resource
    private LuaConverters converters;

    public Instance(D delegate, Injector injector) {
      super(delegate);
      injector.injectMembers(this);
    }

    /**
     * This is true, if this entity is alive, false otherwise.
     */
    @LuaProperty
    public boolean isAlive() {
      return delegate.isAlive();
    }

    /**
     * The 'dimension' is a magic number that tells us something about the world where this entity
     * currently is living in. 0 means the Overworld. -1 is the Nether, and 1 is the End.
     */
    @LuaProperty
    public int getDimension() {
      return delegate.getDimension();
    }

    /**
     * The 'facing' is the compass direction this entity is facing. This is one of 'north', 'east',
     * 'south', and 'west'.
     */
    @LuaProperty
    public EnumFacing getFacing() {
      return delegate.getFacing();
    }

    /**
     * The 'lookVec' is a 3-dimensional vector that points into the direction this entity is looking
     * at, or nil, if it is not looking anywhere, for example, if it has no eyes.
     */
    @LuaProperty
    public @Nullable Vec3d getLookVec() {
      return delegate.getLookVec();
    }

    @LuaProperty
    public void setLookVec(Vec3d lookVec) {
      double pitch = Math.toDegrees(Math.asin(-lookVec.y));
      double yaw = Math.toDegrees(MathHelper.atan2(-lookVec.x, lookVec.z));
      setRotationYawAndPitch((float) yaw, (float) pitch);
    }

    /**
     * The 'motion' is a 3-dimensional vector that represents the velocity of this entity when it is
     * moved by some external force, e.g. when it is falling or when it is pushed by an explosion.
     */
    @LuaProperty
    public Vec3d getMotion() {
      // double x = delegate.motionX;
      // double y = delegate.motionY;
      // double z = delegate.motionZ;
      // return new Vec3d(x, y, z);
      return delegate.getMotion();
    }

    @LuaProperty
    public void setMotion(Vec3d motion) {
      double x = motion.x;
      double y = motion.y;
      double z = motion.z;

      // see SPacketEntityVelocity
      double maxLen = 3.9;
      double lenSqr = x * x + y * y + z * z;
      if (lenSqr > maxLen * maxLen) {
        double f = maxLen / Math.sqrt(lenSqr);
        x = x * f;
        y = y * f;
        z = z * f;
      }

      // delegate.motionX = x;
      // delegate.motionY = y;
      // delegate.motionZ = z;
      // delegate.velocityChanged = true;
      delegate.setMotion(new Vec3d(x, y, z));
    }

    /**
     * The 'name' of this entity is unlike the UUID not unique in the world. For most entities it is
     * just something like 'Pig' or 'Zombie'. For player entities it is the nickkname of the
     * character, like 'mickkay' or 'bytemage'.
     */
    @LuaProperty
    public String getName() {
      return delegate.getName();
    }

    @LuaProperty
    public void setName(String name) {
      delegate.setName(name);
    }

    // /**
    // * The 'nbt' value (short for Named Binary Tag) is a table of entity-specifc key-value pairs
    // also
    // * called [data tags](https://minecraft.gamepedia.com/Commands#Data_tags). The nbt property is
    // * readonly but gives you a modifiable copy of the internal value. You can change the
    // contents,
    // * but to activate them you have to assign the modified table to the entity by using the
    // * [putNbt()](/modules/Entity/#putNbt) function.
    // */
    // @LuaProperty(type = "table")
    // public NBTTagCompound getNbt() {
    // NBTTagCompound nbt = new NBTTagCompound();
    // delegate.writeToNBT(nbt);
    // return nbt;
    // }

    /**
     * The 'pos' is short for 'position'. It is a 3-dimensional vector containing the location of
     * the entity inside the world it is living in.
     */
    @LuaProperty
    public Vec3d getPos() {
      return delegate.getPositionVector();
    }

    @LuaProperty
    public void setPos(Vec3d pos) {
      delegate.setPosition(pos);
    }

    /**
     * The 'rotationPitch' is the rotation of this entity's head around its X axis in degrees. A
     * value of -90 means the entity is looking straight up. A value of 90 means it is looking
     * straight down.
     */
    @LuaProperty
    public float getRotationPitch() {
      return delegate.getRotationPitch();
    }

    @LuaProperty
    public void setRotationPitch(float rotationPitch) {
      delegate.setRotationPitch(rotationPitch);
      // delegate.setPositionAndRotation(delegate.posX, delegate.posY, delegate.posZ,
      // delegate.rotationYaw, rotationPitch);
    }

    /**
     * The 'rotationYaw' is the rotation of this entity around its Y axis in degrees. For example, a
     * value of 0 means the entity is facing south. 90 corresponds to west, and 45 to south-west.
     */
    @LuaProperty
    public float getRotationYaw() {
      // return MathHelper.wrapDegrees(delegate.rotationYaw);
      return delegate.getRotationYaw();
    }

    @LuaProperty
    public void setRotationYaw(float rotationYaw) {
      // delegate.setRotationYawHead(rotationYaw);
      // delegate.setRenderYawOffset(rotationYaw);
      // delegate.setPositionAndRotation(delegate.posX, delegate.posY, delegate.posZ, rotationYaw,
      // delegate.rotationPitch);
      delegate.setRotationYaw(rotationYaw);
    }

    protected void setRotationYawAndPitch(float yaw, float pitch) {
      // delegate.setRotationYawHead(yaw);
      // delegate.setRenderYawOffset(yaw);
      // delegate.setPositionAndRotation(delegate.posX, delegate.posY, delegate.posZ, yaw, pitch);
      delegate.setRotationYawAndPitch(yaw, pitch);
    }

    // /**
    // * This is true, if this entity is currently sneaking, false otherwise.
    // */
    // @LuaProperty
    // public boolean isSneaking() {
    // return delegate.isSneaking();
    // }

    /**
     * The 'tags' value is a list of strings that have been assigned to this entity.
     */
    @LuaProperty
    public Collection<String> getTags() {
      return delegate.getTags();
    }

    @LuaProperty(type = "table")
    public void setTags(Object luaObj) {
      List<String> tags = converters.toJavaList(String.class, luaObj, "tags");
      delegate.setTags(tags);
    }

    /**
     * The 'uuid' is a string of 36 characters forming an immutable universally unique identifier
     * that identifies this entity inside the world. This means if entities have the same ID they
     * are actually the same object.
     */
    @LuaProperty
    public String getUuid() {
      return delegate.getUniqueID().toString();
    }

    /**
     * The world the the space this entity is living in.
     */
    @LuaProperty
    public World getWorld() {
      return delegate.getEntityWorld();
    }

    /**
     * The 'addTag' function adds the given tag to the set of [tags](/modules/Entity/#tags) of this
     * entity. This function returns true if the tag was added successfully.
     */
    @LuaFunction
    public boolean addTag(String tag) {
      return delegate.addTag(tag);
    }

    /**
     * The 'dropItem' function drops the given item at this entity's position modified by the
     * optionally given vertical offset.
     */
    @LuaFunction
    public EntityItem dropItem(ItemStack item, @Nullable Float offsetY) {
      offsetY = ofNullable(offsetY).orElse(0f);
      if (item.getCount() == 0) {
        throw new IllegalArgumentException("Can't drop an item with count==0");
      }
      return delegate.dropItem(item, offsetY);
    }

    /**
     * The 'kill' function kills this entity during the next game tick.
     */
    @LuaFunction
    public void kill() {
      delegate.setDead();
    }

    /**
     * The 'move' function teleports this entity instantly to the position relative to its current
     * position specified by the given direction and distance. If no distance is specified, 1 meter
     * is taken as default distance. Valid direction values are absolute directions ('up', 'down',
     * 'north', 'east', 'south', and 'west'), as well as relative directions ('forward', 'back',
     * 'left', and 'right'). Relative directions are interpreted relative to the direction the
     * entity is [facing](/modules/Entity/#facing).
     */
    @LuaFunction
    public void move(String directionName, @Nullable Double distance) {
      Direction direction = Direction.byName(directionName);
      checkNotNull(direction != null, "expected direction but got %s", direction);
      if (distance == null) {
        distance = 1d;
      }
      Vec3d vec = direction.getDirectionVec(getRotationYaw());
      Vec3d newVec = delegate.getPositionVector().add(vec.scale(distance));

      delegate.setPosition(newVec);
    }

    // /**
    // * The 'putNbt' function inserts the given table entries into the [nbt](/modules/Entity/#nbt)
    // * property of this entity. Please note that this function is not supported for
    // * [Player](/modules/Player/) objects.
    // */
    // @LuaFunction
    // public void putNbt(Table nbt) {
    // NBTTagCompound oldNbt = delegate.serializeNBT();
    // NBTTagCompound newNbt = getConverters().getNbtConverter().merge(oldNbt, nbt);
    // delegate.readFromNBT(newNbt);
    // }

    /**
     * The 'removeTag' function removes the given tag from the set of [tags](/modules/Entity/#tags)
     * of this entity. This function returns true if the tag has been removed successfully, and
     * false if there was no such tag.
     */
    @LuaFunction
    public boolean removeTag(String tag) {
      return delegate.removeTag(tag);
    }

    /**
     * The 'scanView' function scans the view of this entity for the next (non-liquid) block. On
     * success it returns a [BlockHit](/modules/BlockHit/), otherwise nil. It scans the view with a
     * line-of-sight-range of up to the given distance (meter).
     */
    @LuaFunction
    public RayTraceResult scanView(float distance) {
      Vec3d start = delegate.getPositionVector();
      Vec3d end = start.add(delegate.getLookVec().scale(distance));
      return delegate.getEntityWorld().rayTraceBlocks(start, end, false);
    }
  }
}
