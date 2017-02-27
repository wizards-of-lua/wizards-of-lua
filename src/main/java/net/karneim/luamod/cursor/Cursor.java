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
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class Cursor {
  private final World world;
  private BlockPos origin;
  private Rotation initialRotation;
  @Nullable
  private EnumFacing surface;

  private final Stack<Location> locationStack = new Stack<Location>();

  private BlockPos worldPosition;
  private Rotation rotation;
  private ICommandSender owner;
  private ICommandSender luaProcessEntity;

  public Cursor(ICommandSender owner, World world) {
    this(owner, owner, world, BlockPos.ORIGIN, Rotation.NONE, null);
  }

  public Cursor(ICommandSender owner, ICommandSender luaProcessEntity, World world,
      BlockPos worldPosition, @Nullable Rotation rotation, @Nullable EnumFacing surface) {

    this.owner = checkNotNull(owner);
    this.luaProcessEntity = checkNotNull(luaProcessEntity);
    this.world = checkNotNull(world);
    this.origin = checkNotNull(worldPosition);
    this.worldPosition = checkNotNull(worldPosition);
    this.initialRotation = checkNotNull(rotation);
    this.rotation = rotation == null ? Rotation.NONE : rotation;
    this.surface = surface;
  }

  public void setOrigin() {
    origin = worldPosition;
  }

  public World getWorld() {
    return world;
  }

  public BlockPos getOrigin() {
    return origin;
  }

  public @Nullable Vec3d getOwnerPosition() {
    Vec3d pos = getOwnerWorldPosition();
    if (pos != null) {
      return BlockPosUtil.rotate(pos.subtract(new Vec3d(origin)), negate(initialRotation));
    }
    return null;
  }

  public @Nullable Vec3d getOwnerWorldPosition() {
    Entity entity = owner.getCommandSenderEntity();
    if (entity != null) {
      // return entity.getPosition();
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
    return CursorUtil.getFacing(this.rotation);
  }

  public void setOrientation(EnumFacing facing) {
    Rotation rot = CursorUtil.getRotation(facing);
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

  public BlockPos getPosition() {
    return BlockPosUtil.rotate(worldPosition.subtract(origin), negate(initialRotation));
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

  public void setPosition(BlockPos pos) {
    worldPosition = origin.add(BlockPosUtil.rotate(pos, this.rotation));
  }

  public BlockPos getWorldPosition() {
    return worldPosition;
  }

  public void setWorldPosition(BlockPos pos) {
    worldPosition = pos;
  }

  public void move(EnumFacing direction) {
    move(direction, 1);
  }

  public void move(EnumFacing direction, int lenght) {
    worldPosition = worldPosition.offset(direction, lenght);
  }

  public void move(EnumDirection direction) {
    move(direction, 1);
  }

  public void move(EnumDirection direction, int lenght) {
    move(direction.rotate(rotation), lenght);
  }

  public void moveBy(int x, int y, int z) {
    BlockPos diff = BlockPosUtil.rotate(new BlockPos(x, y, z), this.rotation);
    worldPosition = worldPosition.add(diff);
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

  public void place(Block block) {
    IBlockState state = block.getDefaultState();
    state = state.withRotation(rotation.add(Rotation.CLOCKWISE_180));
    world.setBlockState(worldPosition, state);
  }

  public void place(IBlockState blockState) {
    world.setBlockState(worldPosition, blockState);
  }

  public IBlockState getBlockState() {
    return world.getBlockState(worldPosition);
  }

  public Snapshot copy(Selection selection) {
    Snapshot result = new Snapshot();
    result.copyFromWorld(world, worldPosition, rotation, selection);
    return result;
  }

  public Snapshot cut(Selection selection) {
    Snapshot result = new Snapshot();
    result.cutFromWorld(world, worldPosition, rotation, selection);
    return result;
  }

  public Selection paste(Snapshot snapshot) {
    return snapshot.pasteToWorld(world, worldPosition, rotation);
  }

  public boolean isEmpty() {
    return getBlockState().getBlock().equals(Blocks.AIR);
    // return getBlockState().getMaterial() == Material.AIR;
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
