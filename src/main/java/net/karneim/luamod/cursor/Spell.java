package net.karneim.luamod.cursor;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Stack;

import javax.annotation.Nullable;

import net.karneim.luamod.LuaMod;
import net.karneim.luamod.lua.SpellEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class Spell {
  private final World world;
  private Vec3d origin;
  private float initialRotation;
  @Nullable
  private EnumFacing surface;

  private final Stack<Location> locationStack = new Stack<Location>();

  private Vec3d position;
  private float rotation;
  private ICommandSender owner;
  private SpellEntity spellEntity;
  private Snapshots snapshots;

  public Spell(ICommandSender owner, SpellEntity spellEntity, World world,
      Vec3d worldPosition, Rotation rotation, @Nullable EnumFacing surface, Snapshots snapshots) {
    this.owner = checkNotNull(owner);
    this.spellEntity = checkNotNull(spellEntity);
    this.world = checkNotNull(world);
    this.origin = checkNotNull(worldPosition);
    this.position = checkNotNull(worldPosition);
    this.initialRotation = SpellUtil.getRotationYaw(rotation);
    this.rotation = initialRotation;
    this.surface = surface;
    this.snapshots = snapshots;
  }

  public void setOrigin() {
    origin = position;
  }

  public World getWorld() {
    return world;
  }

  public Vec3d getOrigin() {
    return origin;
  }

  public @Nullable Vec3d getOwnerWorldPosition() {
    Entity entity = owner.getCommandSenderEntity();
    if (entity != null) {
      return entity.getPositionVector();
    }
    return null;
  }

  public Entity getOwner() {
    return owner.getCommandSenderEntity();
  }

  public String getOwnerName() {
    return owner.getName();
  }

  public int execute(String command) {
    //LuaMod.instance.logger.info("Execute: "+command);
    return world.getMinecraftServer().getCommandManager().executeCommand(spellEntity, command);
  }

  public Block getBlockByName(String name) throws NumberInvalidException {
    return CommandBase.getBlockByText(spellEntity, name);
  }

  public void pushLocation() {
    this.locationStack.push(new Location(origin, initialRotation));
    this.origin = position;
    this.initialRotation = rotation;
  }

  public boolean popLocation() {
    reset();
    Location loc = this.locationStack.pop();
    if (loc != null) {
      this.origin = loc.getPosition();
      this.initialRotation = loc.getRotation();
      return true;
    }
    return false;
  }

  public float getInitialRotation() {
    return initialRotation;
  }

  public float getRotation() {
    return MathHelper.wrapDegrees(rotation);
  }

  public EnumFacing getOrientation() {
    return SpellUtil.getFacing(this.rotation);
  }

  public void setOrientation(EnumFacing facing) {
    Rotation rot = SpellUtil.getRotation(facing);
    if (rot != null) {
      this.rotation = SpellUtil.getRotationYaw(rot);
    }
  }

  public @Nullable EnumFacing getSurface() {
    return surface;
  }

  public void setRotation(Rotation rotation) {
    setRotation(SpellUtil.getRotationYaw(rotation));
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
    this.spellEntity.updatePosition();
  }

  private Rotation negate(Rotation rot) {
    switch (rot) {
      case NONE:
      case CLOCKWISE_180:
        return rot;
      case CLOCKWISE_90:
        return Rotation.COUNTERCLOCKWISE_90;
      case COUNTERCLOCKWISE_90:
        return Rotation.CLOCKWISE_90;
      default:
        throw new Error("WTF?");
    }
  }

  public Vec3d getPosition() {
    return position;
  }

  public void setPosition(Vec3d pos) {
    this.position = pos;
    this.spellEntity.updatePosition();
  }

  public void move(EnumFacing direction) {
    move(direction, 1);
  }

  public void move(EnumFacing direction, double lenght) {
    setPosition(position.add(new Vec3d(direction.getDirectionVec()).scale(lenght)));
  }

  public void move(EnumDirection direction) {
    move(direction, 1);
  }

  public void move(EnumDirection direction, double lenght) {
    float angle = direction.getHorizontalAngle();
    float yaw = rotation + angle;

    Rotation rot = SpellUtil.roundRotation(yaw, 0.01);
    if (rot != null) {
      EnumFacing facing = SpellUtil.getFacing(rot);
      Vec3d step = new Vec3d(facing.getDirectionVec()).scale(lenght);
      setPosition(position.add(step));
    } else {
      float rad = (float) Math.toRadians(yaw);
      float x = -MathHelper.sin(rad);
      float z = MathHelper.cos(rad);

      Vec3d step = new Vec3d(x, 0, z).scale(lenght);
      setPosition(position.add(step));
    }
  }

  public void moveBy(double x, double y, double z) {
    setPosition(position.addVector(x, y, z));
  }

  public void resetPosition() {
    setPosition(origin);
  }

  public void rotate(Rotation rotation) {
    rotate(SpellUtil.getRotationYaw(rotation));
  }

  public void rotate(float rotationYaw) {
    this.rotation += rotationYaw;
  }

  public void resetRotation() {
    rotation = initialRotation;
  }

  public void setBlock(Block block) {
    IBlockState state = block.getDefaultState();
    Rotation rot = SpellUtil.roundRotation(rotation);
    state = state.withRotation(rot);
    world.setBlockState(new BlockPos(position), state);
  }

  public void setBlockState(IBlockState blockState) {
    world.setBlockState(new BlockPos(position), blockState);
  }

  public IBlockState getBlockState() {
    return world.getBlockState(new BlockPos(position));
  }

  public String copy(Selection selection) {
    Snapshot snapshot = new Snapshot();
    snapshot.copyFromWorld(world, new BlockPos(position), SpellUtil.roundRotation(rotation),
        selection);
    return snapshots.registerSnapshot(snapshot);
  }

  public String cut(Selection selection) {
    Snapshot snapshot = new Snapshot();
    snapshot.cutFromWorld(world, new BlockPos(position), SpellUtil.roundRotation(rotation),
        selection);
    return snapshots.registerSnapshot(snapshot);
  }

  public Selection paste(String id) {
    Snapshot snapshot = snapshots.getSnapshot(id);
    return snapshot.pasteToWorld(world, new BlockPos(position),
        SpellUtil.roundRotation(rotation));
  }

  public void reset() {
    resetPosition();
    resetRotation();
  }

  public void say(String message) {
    world.getMinecraftServer().getCommandManager().executeCommand(spellEntity,
        "say " + message);
  }

  public void msg(String target, String message) {
    world.getMinecraftServer().getCommandManager().executeCommand(spellEntity,
        "msg " + target + " " + message);
  }

  public void print(String message) {
    owner.addChatMessage(new TextComponentString(message));
  }

}
