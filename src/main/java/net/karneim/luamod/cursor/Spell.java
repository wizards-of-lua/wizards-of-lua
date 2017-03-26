package net.karneim.luamod.cursor;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Stack;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class Spell {
  private final World world;
  private Vec3d origin;
  private Rotation initialRotation;
  @Nullable
  private EnumFacing surface;

  private final Stack<Location> locationStack = new Stack<Location>();

  private Vec3d worldPosition;
  private Rotation rotation;
  private ICommandSender owner;
  private ICommandSender luaProcessEntity;
  private Snapshots snapshots;

  public Spell(ICommandSender owner, World world) {
    this(owner, owner, world, Vec3d.ZERO, Rotation.NONE, null, null);
  }

  public Spell(ICommandSender owner, ICommandSender luaProcessEntity, World world,
      Vec3d worldPosition, @Nullable Rotation rotation, @Nullable EnumFacing surface, Snapshots snapshots) {
    this.owner = checkNotNull(owner);
    this.luaProcessEntity = checkNotNull(luaProcessEntity);
    this.world = checkNotNull(world);
    this.origin = checkNotNull(worldPosition);
    this.worldPosition = checkNotNull(worldPosition);
    this.initialRotation = checkNotNull(rotation);
    this.rotation = rotation == null ? Rotation.NONE : rotation;
    this.surface = surface;
    this.snapshots = snapshots;
  }

  public void setOrigin() {
    origin = worldPosition;
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
    return world.getMinecraftServer().getCommandManager().executeCommand(luaProcessEntity, command);
  }

  public Block getBlockByName(String name) throws NumberInvalidException {
    return CommandBase.getBlockByText(luaProcessEntity, name);
  }

  public void pushLocation() {
    this.locationStack.push(new Location(origin, initialRotation));
    this.origin = worldPosition;
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

  public Rotation getInitialRotation() {
    return initialRotation;
  }

  public Rotation getRotation() {
    return rotation;
  }

  public EnumFacing getOrientation() {
    return SpellUtil.getFacing(this.rotation);
  }

  public void setOrientation(EnumFacing facing) {
    Rotation rot = SpellUtil.getRotation(facing);
    if (rot != null) {
      this.rotation = rot;
    }
  }

  public @Nullable EnumFacing getSurface() {
    return surface;
  }

  public void setRotation(Rotation rotation) {
    this.rotation = rotation;
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

  public Vec3d getWorldPosition() {
    return worldPosition;
  }

  public void setWorldPosition(Vec3d pos) {
    worldPosition = pos;
  }

  public void move(EnumFacing direction) {
    move(direction, 1);
  }

  public void move(EnumFacing direction, double lenght) {
    worldPosition = worldPosition.add(new Vec3d(direction.getDirectionVec()).scale(lenght));
  }

  public void move(EnumDirection direction) {
    move(direction, 1);
  }

  public void move(EnumDirection direction, double lenght) {
    move(direction.rotate(rotation), lenght);
  }

  public void moveBy(double x, double y, double z) {
    worldPosition = worldPosition.addVector(x, y, z);
  }

  public void resetPosition() {
    worldPosition = origin;
  }

  public void rotate(Rotation rotation) {
    this.rotation = this.rotation.add(rotation);
  }

  public void resetRotation() {
    rotation = initialRotation;
  }

  public void setBlock(Block block) {
    IBlockState state = block.getDefaultState();
    state = state.withRotation(rotation.add(Rotation.CLOCKWISE_180));
    world.setBlockState(new BlockPos(worldPosition), state);
  }

  public void setBlockState(IBlockState blockState) {
    world.setBlockState(new BlockPos(worldPosition), blockState);
  }

  public IBlockState getBlockState() {
    return world.getBlockState(new BlockPos(worldPosition));
  }

  public Snapshot copy(Selection selection) {
    Snapshot result = new Snapshot();
    result.copyFromWorld(world, new BlockPos(worldPosition), rotation, selection);
    return result;
  }

  public String copySelection(Selection selection) {
    Snapshot snapshot = copy(selection);
    String result = snapshots.registerSnapshot(snapshot);
    return result;
  }

  public Snapshot cut(Selection selection) {
    Snapshot result = new Snapshot();
    result.cutFromWorld(world, new BlockPos(worldPosition), rotation, selection);
    return result;
  }

  public String cutSelection(Selection selection) {
    Snapshot snapshot = cut(selection);
    String result = snapshots.registerSnapshot(snapshot);
    return result;
  }

  public Selection paste(Snapshot snapshot) {
    return snapshot.pasteToWorld(world, new BlockPos(worldPosition), rotation);
  }

  public Selection paste(String id) {
    Snapshot snapshot = snapshots.getSnapshot(id);
    Selection resultSelection = paste(snapshot);
    return resultSelection;
  }


  public void reset() {
    resetPosition();
    resetRotation();
  }

  public void say(String message) {
    world.getMinecraftServer().getCommandManager().executeCommand(luaProcessEntity,
        "say " + message);
  }

  public void msg(String target, String message) {
    world.getMinecraftServer().getCommandManager().executeCommand(luaProcessEntity,
        "msg " + target + " " + message);
  }

  public void print(String message) {
    owner.addChatMessage(new TextComponentString(message));
  }



}
