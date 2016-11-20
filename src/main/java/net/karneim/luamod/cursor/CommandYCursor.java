package net.karneim.luamod.cursor;

import static net.karneim.luamod.cursor.EnumDirection.FORWARD;
import static net.karneim.luamod.cursor.EnumDirection.UP;
import static net.minecraft.util.Rotation.CLOCKWISE_90;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.karneim.luamod.LuaMod;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class CommandYCursor extends CommandBase {

  private static final String CMD_NAME = "ycursor";
  private static final String MSG_USAGE = "commands.ycursor.usage";

  private final LuaMod mod;
  private final List<String> aliases = new ArrayList<String>();

  public CommandYCursor() {
    aliases.add(CMD_NAME);
    mod = LuaMod.instance;
  }

  @Override
  public String getCommandName() {
    return CMD_NAME;
  }

  @Override
  public int getRequiredPermissionLevel() {
    // return 2;
    return 0;
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return MSG_USAGE;
  }

  @Override
  public List getCommandAliases() {
    return aliases;
  }

  @Override
  public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender,
      String[] args, @Nullable BlockPos pos) {
    // TODO
    return Collections.<String>emptyList();
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {

    // createFurnaceTower(sender);

    // copyRowInFront(sender);

    // copyAndPaste(sender);

    // replaceSelection(sender);

    rotateSelection(sender);

  }

  private void rotateSelection(ICommandSender sender) {
    Cursor cursor = CursorUtil.createCursor(sender);

    BlockMask mask = new ExcludeMask(Blocks.AIR);
    SelectionStrategy strategy = new ContinuousSelectionStrategy(mask, 64);
    CustomSelector selector = new CustomSelector(cursor, strategy);
    Selection selection = selector.getSelection();
    Vec3i center = selection.getBoundingBox().getCenter();
    cursor.setWorldPosition(new BlockPos(center));
    Snapshot snapshot = cursor.cut(selection);
    cursor.rotate(Rotation.CLOCKWISE_90);
    cursor.paste(snapshot);
  }

  private void replaceSelection(ICommandSender sender) {
    Cursor cursor = CursorUtil.createCursor(sender);

    BlockMask mask = new ExcludeMask(Blocks.AIR);
    SelectionStrategy strategy = new ContinuousSelectionStrategy(mask, 64);
    CustomSelector selector = new CustomSelector(cursor, strategy);
    Selection selection = selector.getSelection();

    for (BlockPos pos : selection) {
      cursor.setWorldPosition(pos);
      cursor.place(Blocks.WOOL);
    }
  }

  private void copyAndPaste(ICommandSender sender) {
    Cursor cursor = CursorUtil.createCursor(sender);

    BlockMask mask = new ExcludeMask(Blocks.AIR);
    SelectionStrategy strategy = new ContinuousSelectionStrategy(mask, 64);
    CustomSelector selector = new CustomSelector(cursor, strategy);
    Selection selection = selector.getSelection();

    if (!selection.isEmpty()) {
      Snapshot snapshot = cursor.copy(selection);
      cursor.move(EnumDirection.RIGHT, 2);
      cursor.rotate(CLOCKWISE_90);
      cursor.paste(snapshot);
    }
  }

  private void copyRowInFront(ICommandSender sender) {
    Cursor cursor = CursorUtil.createCursor(sender);

    SimpleSelector selector = new SimpleSelector(cursor);
    for (int i = 0; i < 10; ++i) {
      if (!cursor.isEmpty()) {
        selector.select();
      }
      cursor.move(EnumDirection.FORWARD);
    }
    cursor.resetPosition();
    cursor.resetRotation();
    Selection selection = selector.getSelection();
    if (!selection.isEmpty()) {
      Snapshot snapshot = cursor.copy(selection);
      cursor.move(EnumDirection.RIGHT, 4);
      cursor.rotate(CLOCKWISE_90);
      cursor.paste(snapshot);
    }
  }

  private void createFurnaceTower(ICommandSender sender) {
    Cursor cursor = CursorUtil.createCursor(sender);
    // Block block = Blocks.IRON_BLOCK;
    Block block = Blocks.FURNACE;

    for (int y = 0; y < 10; ++y) {
      for (int i = 0; i < 5; ++i) {
        cursor.place(block);
        cursor.move(FORWARD);
      }
      cursor.rotate(CLOCKWISE_90);
      for (int i = 0; i < 5; ++i) {
        cursor.place(block);
        cursor.move(FORWARD);
      }
      cursor.rotate(CLOCKWISE_90);
      for (int i = 0; i < 5; ++i) {
        cursor.place(block);
        cursor.move(FORWARD);
      }
      cursor.rotate(CLOCKWISE_90);
      for (int i = 0; i < 5; ++i) {
        cursor.place(block);
        cursor.move(FORWARD);
      }
      cursor.rotate(CLOCKWISE_90);
      cursor.move(UP);
    }
  }

}
