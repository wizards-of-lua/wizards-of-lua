package net.karneim.luamod.cursor;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class Snapshot {
  static class Content {
    private final StructureBoundingBox sourceBox;
    private final BlockPos pivot;
    private final Rotation originalRotation;
    private List<BlockData> blocksNormal = Lists.newArrayList();
    private List<BlockData> blocksWithTileEntity = Lists.newArrayList();
    private List<BlockData> blocksNotFull = Lists.newArrayList();
    private Deque<BlockPos> allSourcePositions = Lists.newLinkedList();
    private List<ScheduledBlockUpdate> scheduledBlockUpdates = Lists.newArrayList();

    public Content(StructureBoundingBox boundingBox, BlockPos pivot, Rotation rotation) {
      this.sourceBox = boundingBox;
      this.pivot = pivot;
      this.originalRotation = rotation;
    }

    /**
     * Returns a list of all block positions inside the source bounding box.
     * 
     * @return a list of all block positions inside the source bounding box
     */
    private List<BlockPos> getBlockPositions() {
      List<BlockPos> result = new ArrayList<BlockPos>();
      for (int z = sourceBox.minZ; z <= sourceBox.maxZ; ++z) {
        for (int y = sourceBox.minY; y <= sourceBox.maxY; ++y) {
          for (int x = sourceBox.minX; x <= sourceBox.maxX; ++x) {
            BlockPos sourcePos = new BlockPos(x, y, z);
            result.add(sourcePos);
          }
        }
      }
      return result;
    }

    public void addBlockWithTileEntity(BlockData blockData) {
      blocksWithTileEntity.add(blockData);
      allSourcePositions.addLast(blockData.sourcePos);
    }

    public void addBlockNotFull(BlockData blockData) {
      blocksNotFull.add(blockData);
      // Put this position to the start of the list in order to make
      // sure that these blocks are deleted before the others when in cut mode.
      allSourcePositions.addFirst(blockData.sourcePos);
    }

    public void addBlockNormal(BlockData blockData) {
      blocksNormal.add(blockData);
      allSourcePositions.addLast(blockData.sourcePos);
    }

    public void addScheduledBlockUpdate(ScheduledBlockUpdate scheduledBlockUpdate) {
      scheduledBlockUpdates.add(scheduledBlockUpdate);
    }

    public Iterable<BlockPos> getAllSourcePositions() {
      return allSourcePositions;
    }

    public List<BlockData> getAllBlockData() {
      List<BlockData> result = Lists.newArrayList();
      result.addAll(blocksNormal);
      result.addAll(blocksWithTileEntity);
      result.addAll(blocksNotFull);
      return result;
    }

    public BlockPos getPivot() {
      return pivot;
    }

    public Iterable<BlockData> getBlocksWithTileEntity() {
      return blocksWithTileEntity;
    }

    public Iterable<ScheduledBlockUpdate> getScheduledBlockUpdates() {
      return scheduledBlockUpdates;
    }
  }

  private Content content;

  public void copyFromWorld(World world, BlockPos pivot, Rotation rotation, Selection selection) {
    if (selection.isEmpty()) {
      this.content = null;
      return;
    }
    StructureBoundingBox boundingBox = selection.getBoundingBox();
    this.content = new Content(boundingBox, pivot, rotation);
    // Get all block positions from the source area
    List<BlockPos> sourceBlockPositions = content.getBlockPositions();
    // Copy all block data from source area
    for (BlockPos sourcePos : sourceBlockPositions) {
      IBlockState sourceState = world.getBlockState(sourcePos);

      if (selection.contains(sourcePos)) {
        @Nullable
        TileEntity tileentity = world.getTileEntity(sourcePos);

        if (tileentity != null) {
          // This block has an assigned tile entity (e.g. a chest or a sign)
          NBTTagCompound tileEntityData = tileentity.writeToNBT(new NBTTagCompound());
          content.addBlockWithTileEntity(new BlockData(sourcePos, sourceState, tileEntityData));
        } else if (!sourceState.isFullBlock() && !sourceState.isFullCube()) {
          // This is not a full block (can be e.g. a half block like a slab)
          // and not a full cube (can be e.g. air, vine, anvil, torch)
          content.addBlockNotFull(new BlockData(sourcePos, sourceState, (NBTTagCompound) null));
        } else {
          // This is a 'normal' block
          content.addBlockNormal(new BlockData(sourcePos, sourceState, (NBTTagCompound) null));
        }
      }
    }

    // Loop through all scheduled block updates in the source area
    // and store a scheduled block update for later use.
    List<NextTickListEntry> pendingBlockUpdates = world.getPendingBlockUpdates(boundingBox, false);
    if (pendingBlockUpdates != null) {
      for (NextTickListEntry tickEntry : pendingBlockUpdates) {
        if (boundingBox.isVecInside(tickEntry.position)) {
          BlockPos sourcePos = tickEntry.position;
          content.addScheduledBlockUpdate(new ScheduledBlockUpdate(sourcePos, tickEntry.getBlock(),
              (int) (tickEntry.scheduledTime - world.getWorldInfo().getWorldTotalTime()),
              tickEntry.priority));
        }
      }
    }

  }

  public void cutFromWorld(World world, BlockPos pivot, Rotation rotation, Selection selection) {
    copyFromWorld(world, pivot, rotation, selection);

    Iterable<BlockPos> positions = content.getAllSourcePositions();
    for (BlockPos sourcePos : positions) {
      @Nullable
      TileEntity tileEntity = world.getTileEntity(sourcePos);

      // Make sure to 'empty' all blocks holding an inventory
      if (tileEntity instanceof IInventory) {
        ((IInventory) tileEntity).clear();
      }
      // Before deleting the block make sure that connected blocks do not get destroyed.
      // This is done by replacing the source block with a barrier block first.
      world.setBlockState(sourcePos, Blocks.BARRIER.getDefaultState(), 2);
    }
    // Now safely delete all source blocks
    for (BlockPos sourcePos : positions) {
      world.setBlockState(sourcePos, Blocks.AIR.getDefaultState(), 3);
    }
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

  public Selection pasteToWorld(World world, BlockPos toPos, Rotation rotation) {
    if (content == null) {
      return new Selection();
    }

    Transformation transformation = new Transformation(content.getPivot(),
        rotation.add(negate(content.originalRotation)), toPos.subtract(content.getPivot()));
    
    List<BlockData> allBlockData = content.getAllBlockData();

    // Prepare the target area by deleting all blocks in reverse order,
    // which means:
    // 1) blocksNotFull
    // 2) blocksWithTileEntity
    // 3) blocksNormal
    List<BlockData> reverseOrderedBlockData = Lists.reverse(allBlockData);
    List<BlockPos> targetBlockPositions =
        transformation.transform(BlockData.selectBlockPos(reverseOrderedBlockData));

    for (BlockPos targetPos : targetBlockPositions) {
      @Nullable
      TileEntity tileEntity = world.getTileEntity(targetPos);
      // Make sure to 'empty' all blocks holding an inventory
      if (tileEntity instanceof IInventory) {
        ((IInventory) tileEntity).clear();
      }
      // Before deleting the block make sure that connected blocks do not get destroyed.
      // This is done by replacing the source block with a barrier block first.
      // Doing so makes sure that no items are dropped.
      world.setBlockState(targetPos, Blocks.BARRIER.getDefaultState(), 2);
    }

    // Loop through all copied blocks and paste the block states into the target area.
    // And recalculate the number of affected blocks.
    int numberOfAffectedBlocks = 0;
    for (BlockData data : allBlockData) {
      // Paste the block state from the data into the new position
      IBlockState targetState = transformation.rotate(data.sourceState);
      if (world.setBlockState(transformation.transform(data.sourcePos), targetState, 2)) {
        ++numberOfAffectedBlocks;
      }
    }

    // Loop through copied tile entities and paste them into the target area.
    for (BlockData data : content.getBlocksWithTileEntity()) {
      // Shouldn't be null since the block should have created the tile entity
      // during the paste action above.
      BlockPos targetPos = transformation.transform(data.sourcePos);
      @Nullable
      TileEntity tileEntity = world.getTileEntity(targetPos);
      // But do a paranoid null-check
      if (data.tileEntityData != null && tileEntity != null) {
        // Replace the source coordinates with the target coordinates
        data.tileEntityData.setInteger("x", targetPos.getX());
        data.tileEntityData.setInteger("y", targetPos.getY());
        data.tileEntityData.setInteger("z", targetPos.getZ());
        // Paste the content from the data into the new tile entity
        tileEntity.readFromNBT(data.tileEntityData);
        // Mark it as dirty to ensure that it will be transmitted to all clients
        tileEntity.markDirty();
      }
      // Again paste the block state from the data into the new position.
      // However, I'm not sure why this is necessary.
      IBlockState targetState = transformation.rotate(data.sourceState);
      world.setBlockState(targetPos, targetState, 2);
    }

    // Loop through all blocks (in reverse order) and notify their neighbors
    for (BlockData data : reverseOrderedBlockData) {
      world.notifyNeighborsRespectDebug(transformation.transform(data.sourcePos),
          data.sourceState.getBlock());
    }

    // Loop through all scheduled block updates
    // and schedule another block update for the corresponding block inside the target area.
    for (ScheduledBlockUpdate entry : content.getScheduledBlockUpdates()) {
      BlockPos sourcePos = entry.sourcePos;
      BlockPos targetPos = transformation.transform(sourcePos);
      // System.out.println("Scheduling block update at " + targetPos);
      world.scheduleBlockUpdate(targetPos, entry.block, entry.delay, entry.priority);
    }

    Selection result = new Selection();
    result.addAll(targetBlockPositions);
    return result;
  }

  public Selection getSourceBlockPosSelection() {
    Selection result = new Selection();
    for (BlockPos pos : content.getAllSourcePositions()) {
      result.add(pos);
    }
    return result;
  }

  static class Transformation {
    private final BlockPos pivot;
    private final Rotation rotation;
    private final BlockPos translation;

    public Transformation(BlockPos pivot, Rotation rotation, BlockPos translation) {
      this.pivot = pivot;
      this.rotation = rotation;
      this.translation = translation;
    }

    public BlockPos transform(BlockPos origPos) {
      BlockPos tmpPos = origPos.subtract(pivot);
      BlockPos tmpPos2 = BlockPosUtil.rotate(tmpPos, rotation);
      BlockPos tmpPos3 = tmpPos2.add(pivot);
      BlockPos result = tmpPos3.add(translation);
      return result;
    }

    public List<BlockPos> transform(List<BlockPos> input) {
      List<BlockPos> result = new ArrayList<BlockPos>();
      for (BlockPos blockPos : input) {
        BlockPos targetPos = transform(blockPos);
        result.add(targetPos);
      }
      return result;
    }

    public IBlockState rotate(IBlockState blkState) {
      return BlockPosUtil.rotate(blkState, rotation);
    }
  }


}
