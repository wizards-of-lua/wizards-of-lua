package net.wizardsoflua.spell;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.wizardsoflua.block.ItemUtil;

public class VirtualEntity implements ICommandSender {

  private World world;
  private MinecraftServer server;

  private UUID uuid;
  private String name;

  protected double posX;
  protected double posY;
  protected double posZ;

  protected double motionX;
  protected double motionY;
  protected double motionZ;

  protected float rotationYaw;
  protected float rotationPitch;

  private boolean alive;
  private int dimension;

  private int age;

  private final Set<String> tags = new HashSet<>();

  public VirtualEntity(World world) {
    this.world = world;
    server = world.getMinecraftServer();
    this.uuid = UUID.randomUUID();
    alive = true;
  }

  public UUID getUniqueID() {
    return uuid;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setDead() {
    alive = false;
    // FIXME remove from tick listener
  }

  public boolean isAlive() {
    return alive;
  }

  public int getDimension() {
    return dimension;
  }

  public EnumFacing getFacing() {
    return EnumFacing
        .getHorizontal(MathHelper.floor((double) (this.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);
  }

  public Vec3d getMotion() {
    return new Vec3d(motionX, motionY, motionZ);
  }

  public void setMotion(Vec3d motion) {
    motionX = motion.x;
    motionY = motion.y;
    motionZ = motion.z;
  }

  public float getRotationPitch() {
    return rotationPitch;
  }

  public void setRotationPitch(float rotationPitch) {
    this.rotationPitch = rotationPitch;
  }

  public float getRotationYaw() {
    return rotationYaw;
  }

  public void setRotationYaw(float rotationYaw) {
    this.rotationYaw = MathHelper.wrapDegrees(rotationYaw);;
  }

  public void setRotationYawAndPitch(float yaw, float pitch) {
    setRotationYaw(yaw);
    setRotationPitch(pitch);
  }

  public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
    setPosition(x, y, z);
    setRotationYawAndPitch(yaw, pitch);
  }

  public void setPosition(double x, double y, double z) {
    posX = x;
    posY = y;
    posZ = z;
  }

  public void setPosition(Vec3d pos) {
    setPosition(pos.x, pos.y, pos.z);
  }

  public Vec3d getLookVec() {
    return this.getVectorForRotation(this.rotationPitch, this.rotationYaw);
  }

  /**
   * Creates a Vec3 using the pitch and yaw of the entities rotation.
   */
  protected final Vec3d getVectorForRotation(float pitch, float yaw) {
    float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
    float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
    float f2 = -MathHelper.cos(-pitch * 0.017453292F);
    float f3 = MathHelper.sin(-pitch * 0.017453292F);
    return new Vec3d((double) (f1 * f2), (double) f3, (double) (f * f2));
  }

  public double getDistanceSq(VirtualEntity entity) {
    return this.getPositionVector().squareDistanceTo(entity.getPositionVector());
  }

  public double getDistanceSq(Entity entity) {
    return this.getPositionVector().squareDistanceTo(entity.getPositionVector());
  }

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Collection<String> tags) {
    tags.clear();
    tags.addAll(tags);
  }

  public boolean addTag(String tag) {
    return tags.add(tag);
  }

  public boolean removeTag(String tag) {
    return tags.remove(tag);
  }

  public EntityItem dropItem(ItemStack item, @Nullable Float offsetY) {
    Vec3d pos = getPositionVector();
    return ItemUtil.dropItem(world, pos, item);
  }

  @Override
  public Vec3d getPositionVector() {
    return new Vec3d(posX, posY, posZ);
  }

  @Override
  public BlockPos getPosition() {
    return new BlockPos(posX, posY, posZ);
  }

  @Override
  public World getEntityWorld() {
    return world;
  }

  @Override
  public boolean canUseCommand(int permLevel, String commandName) {
    return true;
  }

  @Override
  public MinecraftServer getServer() {
    return server;
  }

  public void sendMessage(ITextComponent component) {
    // TODO check if we need to do something here
  }

  public void onUpdate() {
    age++;
    // TODO check if this is the correct speed
    posX += motionX;
    posY += motionY;
    posZ += motionZ;
  }

}
