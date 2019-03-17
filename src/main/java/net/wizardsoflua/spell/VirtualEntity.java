package net.wizardsoflua.spell;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.block.ItemUtil;

public class VirtualEntity implements ICommandSource {

  private final World world;
  private final MinecraftServer server;
  private final VirtualEntityChunkLoaderSupport chunkLoaderSupport;

  private UUID uuid;
  private boolean alive;

  protected double posX;
  protected double posY;
  protected double posZ;

  private String name;

  protected double motionX;
  protected double motionY;
  protected double motionZ;
  protected float rotationYaw;
  protected float rotationPitch;
  private int age;
  private final Set<String> tags = new HashSet<>();

  public VirtualEntity(World world, Vec3d position) {
    this.world = world;
    server = world.getServer();
    posX = position.x;
    posY = position.y;
    posZ = position.z;
    chunkLoaderSupport = new VirtualEntityChunkLoaderSupport(WizardsOfLua.instance, this);
    uuid = UUID.randomUUID();
    alive = true;
    setForceChunk(true);
  }

  public void setForceChunk(boolean value) {
    if (value) {
      chunkLoaderSupport.requestNewTicket();
    } else {
      chunkLoaderSupport.releaseTicket();
    }
  }

  public boolean isForceChunk() {
    return chunkLoaderSupport.hasTicket();
  }

  public UUID getUniqueID() {
    return uuid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ITextComponent getDisplayName() {
    return new TextComponentString(getName());
  }

  public int getAge() {
    return age;
  }

  public void setDead() {
    alive = false;
    chunkLoaderSupport.releaseTicket();
  }

  public boolean isAlive() {
    return alive;
  }

  public int getDimension() {
    return world.getDimension().getType().getId();
  }

  public EnumFacing getFacing() {
    return EnumFacing.byHorizontalIndex(MathHelper.floor(rotationYaw * 4.0F / 360.0F + 0.5D) & 3);
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
    this.rotationYaw = MathHelper.wrapDegrees(rotationYaw);
  }

  public void setRotationYawAndPitch(float yaw, float pitch) {
    setRotationYaw(yaw);
    setRotationPitch(pitch);
  }

  /**
   * returns the Entity's pitch and yaw as a Vec2f
   */
  public Vec2f getPitchYaw() {
    return new Vec2f(rotationPitch, rotationYaw);
  }

  public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
    setPosition(x, y, z);
    setRotationYawAndPitch(yaw, pitch);
  }

  public void setPosition(double x, double y, double z) {
    posX = x;
    posY = y;
    posZ = z;
    chunkLoaderSupport.updatePosition();
  }

  public void setPosition(Vec3d pos) {
    setPosition(pos.x, pos.y, pos.z);
  }

  public Vec3d getLookVec() {
    return getVectorForRotation(rotationPitch, rotationYaw);
  }

  /**
   * Creates a Vec3 using the pitch and yaw of the entities rotation.
   */
  protected final Vec3d getVectorForRotation(float pitch, float yaw) {
    float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
    float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
    float f2 = -MathHelper.cos(-pitch * 0.017453292F);
    float f3 = MathHelper.sin(-pitch * 0.017453292F);
    return new Vec3d(f1 * f2, f3, f * f2);
  }

  public double getDistanceSq(VirtualEntity entity) {
    return getPositionVector().squareDistanceTo(entity.getPositionVector());
  }

  public double getDistanceSq(Entity entity) {
    return getPositionVector().squareDistanceTo(entity.getPositionVector());
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

  public Vec3d getPositionVector() {
    return new Vec3d(posX, posY, posZ);
  }

  public BlockPos getPosition() {
    return new BlockPos(posX, posY, posZ);
  }

  public World getWorld() {
    return world;
  }

  public int getPermissionLevel() {
    return 0;
  }

  public MinecraftServer getServer() {
    return server;
  }

  @Override
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

  @Override
  public boolean shouldReceiveFeedback() {
    return true;
  }

  @Override
  public boolean shouldReceiveErrors() {
    return true;
  }

  @Override
  public boolean allowLogging() {
    return true;
  }

  public CommandSource getCommandSource() {
    ICommandSource source = this;
    Vec3d pos = getPositionVector();
    Vec2f pitchYaw = getPitchYaw();
    WorldServer world = this.world instanceof WorldServer ? (WorldServer) this.world : null;
    int permissionLevel = getPermissionLevel();
    String name = getName();
    ITextComponent displayName = getDisplayName();
    MinecraftServer server = getServer();
    Entity entity = null;
    return new WolCommandSource(source, pos, pitchYaw, world, permissionLevel, name, displayName,
        server, entity);
  }

}
