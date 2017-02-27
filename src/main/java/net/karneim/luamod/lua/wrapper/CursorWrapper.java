package net.karneim.luamod.lua.wrapper;

import net.karneim.luamod.cursor.Cursor;
import net.karneim.luamod.cursor.EnumDirection;
import net.karneim.luamod.cursor.Selection;
import net.karneim.luamod.cursor.Snapshot;
import net.karneim.luamod.cursor.Snapshots;
import net.karneim.luamod.lua.event.Events;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction0;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.AbstractFunction3;
import net.sandius.rembulan.runtime.AbstractFunctionAnyArg;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class CursorWrapper {

  public static void installInto(Table env, Cursor cursor, Events eventManager,
      Snapshots snapshots) {
    CursorWrapper wrapper = new CursorWrapper(cursor, eventManager, snapshots);
    env.rawset("cursor", wrapper.getLuaTable());
  }

  private final Cursor cursor;
  private final Events eventManager;
  private final Snapshots snapshots;

  private final Table luaTable = DefaultTable.factory().newTable();

  public CursorWrapper(Cursor cursor, Events eventManager, Snapshots snapshots) {
    this.cursor = cursor;
    this.eventManager = eventManager;
    this.snapshots = snapshots;
    luaTable.rawset("move", new MoveFunction());
    luaTable.rawset("moveBy", new MoveByFunction());
    luaTable.rawset("setPosition", new SetPositionFunction());
    luaTable.rawset("getPosition", new GetPositionFunction());
    luaTable.rawset("getOwnerPosition", new GetOwnerPositionFunction());
    luaTable.rawset("getOwnerWorldPosition", new GetOwnerWorldPositionFunction());
    luaTable.rawset("getOwnerName", new GetOwnerNameFunction());
    luaTable.rawset("getOwner", new GetOwnerFunction());
    luaTable.rawset("setWorldPosition", new SetWorldPositionFunction());
    luaTable.rawset("getWorldPosition", new GetWorldPositionFunction());
    luaTable.rawset("rotate", new RotateFunction());
    luaTable.rawset("setRotation", new SetRotationFunction());
    luaTable.rawset("getRotation", new GetRotationFunction());
    luaTable.rawset("setOrientation", new SetOrientationFunction());
    luaTable.rawset("getOrientation", new GetOrientationFunction());
    luaTable.rawset("getSurface", new GetSurfaceFunction());
    luaTable.rawset("place", new PlaceFunction());
    luaTable.rawset("getBlock", new GetBlockFunction());
    luaTable.rawset("say", new SayFunction());
    luaTable.rawset("msg", new MsgFunction());
    luaTable.rawset("execute", new ExecuteFunction());
    luaTable.rawset("reset", new ResetFunction());
    luaTable.rawset("resetRotation", new ResetRotationFunction());
    luaTable.rawset("resetPosition", new ResetPositionFunction());
    luaTable.rawset("pushLocation", new PushLocationFunction());
    luaTable.rawset("popLocation", new PopLocationFunction());
    luaTable.rawset("cut", new CutFunction());
    luaTable.rawset("copy", new CopyFunction());
    luaTable.rawset("paste", new PasteFunction());
    luaTable.rawset("inAir", new InAirFunction());
  }

  public Table getLuaTable() {
    return luaTable;
  }

  private class MoveFunction extends AbstractFunction2 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      // System.out.println("move: " + arg1 + "," + arg2);
      if (arg2 == null) {
        arg2 = 1;
      }
      if (!(arg2 instanceof Number)) {
        throw new IllegalArgumentException(
            String.format("Integer value expected but got %s!", arg2));
      }
      int length = ((Number) arg2).intValue();
      EnumFacing x = EnumFacing.byName(String.valueOf(arg1));
      if (x != null) {
        cursor.move(x, length);
      } else {
        EnumDirection y = EnumDirection.byName(String.valueOf(arg1));
        if (y != null) {
          cursor.move(y, length);
        } else if ("SURFACE".equals(String.valueOf(arg1))) {
          cursor.move(cursor.getSurface(), length);
        } else {
          throw new IllegalArgumentException(
              String.format("Direction value expected but got %s!", arg1));
        }
      }
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class MoveByFunction extends AbstractFunction3 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2, Object arg3)
        throws ResolvedControlThrowable {
      // System.out.println("moveBy: " + arg1 + "," + arg2 + "," + arg3);
      if (arg1 == null || !(arg1 instanceof Number)) {
        throw new IllegalArgumentException(
            String.format("Integer value for dx expected but got %s!", arg1));
      }
      if (arg2 == null || !(arg2 instanceof Number)) {
        throw new IllegalArgumentException(
            String.format("Integer value for dy expected but got %s!", arg2));
      }
      if (arg3 == null || !(arg3 instanceof Number)) {
        throw new IllegalArgumentException(
            String.format("Integer value for dz expected but got %s!", arg3));
      }

      int dx = ((Number) arg1).intValue();
      int dy = ((Number) arg2).intValue();
      int dz = ((Number) arg3).intValue();
      cursor.moveBy(dx, dy, dz);
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  // private class SetOriginFunction extends AbstractFunction0 {
  // @Override
  // public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
  // // System.out.println("setOrigin");
  // cursor.setOrigin();
  // context.getReturnBuffer().setTo();
  // }
  //
  // @Override
  // public void resume(ExecutionContext context, Object suspendedState)
  // throws ResolvedControlThrowable {
  // throw new NonsuspendableFunctionException();
  // }
  //
  // }

  private class SetPositionFunction extends AbstractFunction3 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2, Object arg3)
        throws ResolvedControlThrowable {
      // System.out.println("setPosition: " + arg1 + "," + arg2 + "," + arg3);
      if (arg1 == null || !(arg1 instanceof Number)) {
        throw new IllegalArgumentException(
            String.format("Integer value for x expected but got %s!", arg1));
      }
      if (arg2 == null || !(arg2 instanceof Number)) {
        throw new IllegalArgumentException(
            String.format("Integer value for y expected but got %s!", arg2));
      }
      if (arg3 == null || !(arg3 instanceof Number)) {
        throw new IllegalArgumentException(
            String.format("Integer value for z expected but got %s!", arg3));
      }

      int x = ((Number) arg1).intValue();
      int y = ((Number) arg2).intValue();
      int z = ((Number) arg3).intValue();
      cursor.setPosition(new BlockPos(x, y, z));

      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class GetPositionFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      // System.out.println("getPosition");
      BlockPos result = cursor.getPosition();
      context.getReturnBuffer().setTo(result.getX(), result.getY(), result.getZ());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class GetOwnerPositionFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      // System.out.println("getOwnerPosition");
      Vec3d result = cursor.getOwnerPosition();
      if (result != null) {
        context.getReturnBuffer().setTo(result.xCoord, result.yCoord, result.zCoord);
      } else {
        context.getReturnBuffer().setTo(null);
      }
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class GetOwnerWorldPositionFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      // System.out.println("getOwnerWorldPosition");
      Vec3d result = cursor.getOwnerWorldPosition();
      if (result != null) {
        context.getReturnBuffer().setTo(result.xCoord, result.yCoord, result.zCoord);
      } else {
        context.getReturnBuffer().setTo(null);
      }
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class GetOwnerNameFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      // System.out.println("getOwnerName");
      String result = cursor.getOwnerName();
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
  
  private class GetOwnerFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      // System.out.println("getOwner");
      Entity result = cursor.getOwner();
      if ( result instanceof EntityPlayer) {
        EntityPlayer player = (EntityPlayer)result;
        EntityPlayerWrapper wrapper = new EntityPlayerWrapper(player);
        context.getReturnBuffer().setTo(wrapper.getLuaObject());
      } else if (result != null){
        EntityWrapper wrapper = new EntityWrapper(result);
        context.getReturnBuffer().setTo(wrapper.getLuaObject());
      } else {
        context.getReturnBuffer().setTo(null);
      }
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }


  private class SetWorldPositionFunction extends AbstractFunction3 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2, Object arg3)
        throws ResolvedControlThrowable {
      // System.out.println("setWorldPosition: " + arg1 + "," + arg2 + "," + arg3);
      if (arg1 == null || !(arg1 instanceof Number)) {
        throw new IllegalArgumentException(
            String.format("Integer value for x expected but got %s!", arg1));
      }
      if (arg2 == null || !(arg2 instanceof Number)) {
        throw new IllegalArgumentException(
            String.format("Integer value for y expected but got %s!", arg2));
      }
      if (arg3 == null || !(arg3 instanceof Number)) {
        throw new IllegalArgumentException(
            String.format("Integer value for z expected but got %s!", arg3));
      }

      int x = ((Number) arg1).intValue();
      int y = ((Number) arg2).intValue();
      int z = ((Number) arg3).intValue();
      cursor.setWorldPosition(new BlockPos(x, y, z));

      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class GetWorldPositionFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      // System.out.println("getWorldPosition");
      BlockPos result = cursor.getWorldPosition();
      context.getReturnBuffer().setTo(result.getX(), result.getY(), result.getZ());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class RotateFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      // System.out.println("rotate: " + arg1);
      Rotation x = rotationByName(String.valueOf(arg1));
      if (x != null) {
        cursor.rotate(x);
      } else {
        throw new IllegalArgumentException(
            String.format("Rotation value expected but got %s!", arg1));
      }
      context.getReturnBuffer().setTo();
    }

    private Rotation rotationByName(String name) {
      return Rotation.valueOf(name);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class SetRotationFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      // System.out.println("setRotation: " + arg1);
      Rotation x = rotationByName(String.valueOf(arg1));
      if (x != null) {
        cursor.setRotation(x);
      } else {
        throw new IllegalArgumentException(
            String.format("Rotation value expected but got %s!", arg1));
      }
      context.getReturnBuffer().setTo();
    }

    private Rotation rotationByName(String name) {
      return Rotation.valueOf(name);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class GetRotationFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      // System.out.println("getRotation");
      Rotation result = cursor.getRotation();
      context.getReturnBuffer().setTo(result.name());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class SetOrientationFunction extends AbstractFunction1 {
    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      // System.out.println("setOrientation: " + arg1);
      EnumFacing f = EnumFacing.byName(String.valueOf(arg1));
      if (f != null) {
        cursor.setOrientation(f);
      } else {
        throw new IllegalArgumentException(
            String.format("Facing value expected but got %s!", arg1));
      }
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class GetOrientationFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      // System.out.println("getOrientation");
      EnumFacing result = cursor.getOrientation();
      context.getReturnBuffer().setTo(result.name());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class GetSurfaceFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      // System.out.println("getSurface");
      EnumFacing result = cursor.getSurface();
      if (result != null) {
        context.getReturnBuffer().setTo(result.name());
      } else {
        context.getReturnBuffer().setTo(null);
      }
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class PlaceFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      // System.out.println("place: " + arg1);
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("Block value expected but got nil!"));
      }
      try {
        Block block = null;
        if (arg1 instanceof DelegatingTable) {
          DelegatingTable table = (DelegatingTable) arg1;
          String typename = String.valueOf(table.rawget("type"));
          if ("Block".equals(typename)) {
            IBlockState blockState = (IBlockState) table.getDelegate();
            cursor.place(blockState);
            context.getReturnBuffer().setTo();
            return;
          } else {
            throw new IllegalArgumentException(
                String.format("Block value expected but got %s!", arg1));
          }
        } else if (arg1 instanceof Table) {
          Table table = (Table) arg1;
          String typename = String.valueOf(table.rawget("type"));
          String name = String.valueOf(table.rawget("name"));
          if ("Block".equals(typename)) {
            block = cursor.getBlockByName(name);
          }
        } else {
          block = cursor.getBlockByName(String.valueOf(arg1));
        }
        if (block != null) {
          cursor.place(block);
        } else {
          throw new IllegalArgumentException(
              String.format("Block value expected but got %s!", arg1));
        }
      } catch (NumberInvalidException e) {
        throw new IllegalArgumentException(
            String.format("Block value expected but received exception: %s!", e.getMessage()));
      }
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class GetBlockFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      // System.out.println("getBlock");
      IBlockState blockState = cursor.getBlockState();
      String result = blockState.getBlock().getRegistryName().getResourcePath();
      BlockStateWrapper wrapper = new BlockStateWrapper(blockState);
      context.getReturnBuffer().setTo(wrapper.getLuaObject());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class SayFunction extends AbstractFunctionAnyArg {

    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      // System.out.println("say: " + args);
      if (args == null) {
        args = new String[] {""};
      }
      String text = concat("\t", args);
      cursor.say(encode(text));
      context.getReturnBuffer().setTo();
    }

    private String concat(String delimter, Object[] args) {
      StringBuilder result = new StringBuilder();
      for (int i = 0; i < args.length; ++i) {
        if (i > 0) {
          result.append(delimter);
        }
        if (args[i] == null) {
          result.append("nil");
        } else {
          result.append(String.valueOf(args[i]));
        }
      }
      return result.toString();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class MsgFunction extends AbstractFunction2 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      // System.out.println("msg: " + arg1);
      if (arg1 == null) {
        throw new IllegalArgumentException(
            String.format("Entity name value expected but got nil!"));
      }
      if (arg2 == null) {
        throw new IllegalArgumentException(String.format("Text value expected but got nil!"));
      }
      cursor.msg(String.valueOf(arg1), encode(String.valueOf(arg2)));
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class ExecuteFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      // System.out.println("execute: " + arg1);
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("Text value expected but got nil!"));
      }
      String command = String.valueOf(arg1);
      int result = cursor.execute(command);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class ResetFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      // System.out.println("reset");
      cursor.reset();
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class ResetRotationFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      // System.out.println("resetRotation");
      cursor.resetRotation();
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class ResetPositionFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      // System.out.println("resetPosition");
      cursor.resetPosition();
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class PushLocationFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      // System.out.println("pushLocation");
      cursor.pushLocation();
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class PopLocationFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      // System.out.println("popLocation");
      boolean result = cursor.popLocation();
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class CutFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      // System.out.println("cut: " + arg1);
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("Array of vectors expected but got nil!"));
      }
      if (!(arg1 instanceof Table)) {
        throw new IllegalArgumentException(
            String.format("Array of vectors expected but got %s!", arg1));
      }
      Table table = (Table) arg1;

      Selection selection = new Selection();
      Object k = table.initialKey();
      while (k != null) {
        // process the key k
        Object v = table.rawget(k);
        BlockPos pos = toBlockPos(v);
        selection.add(pos);
        k = table.successorKeyOf(k);
      }
      Snapshot snapshot = cursor.cut(selection);
      String result = snapshots.registerSnapshot(snapshot);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class CopyFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      // System.out.println("copy: " + arg1);
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("Array of vectors expected but got nil!"));
      }
      if (!(arg1 instanceof Table)) {
        throw new IllegalArgumentException(
            String.format("Array of vectors expected but got %s!", arg1));
      }
      Table table = (Table) arg1;

      Selection selection = new Selection();
      Object k = table.initialKey();
      while (k != null) {
        // process the key k
        Object v = table.rawget(k);
        BlockPos pos = toBlockPos(v);
        selection.add(pos);
        k = table.successorKeyOf(k);
      }
      Snapshot snapshot = cursor.copy(selection);
      String result = snapshots.registerSnapshot(snapshot);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class PasteFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      // System.out.println("paste: " + arg1);
      if (arg1 == null) {
        throw new IllegalArgumentException(
            String.format("String being a snapshot id expected, but got nil!"));
      }
      String id = String.valueOf(arg1);

      Snapshot snapshot = snapshots.getSnapshot(id);
      Selection resultSelection = cursor.paste(snapshot);

      Table result = DefaultTable.factory().newTable();
      long idx = 0;
      for (BlockPos pos : resultSelection.getPositions()) {
        idx++;
        Table vec3d = toVec3dTable(pos);
        result.rawset(idx, vec3d);
      }
      context.getReturnBuffer().setTo(result);
    }

    private Table toVec3dTable(BlockPos pos) {
      Table result = DefaultTable.factory().newTable();
      result.rawset("x", pos.getX());
      result.rawset("y", pos.getY());
      result.rawset("z", pos.getZ());
      return result;
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class InAirFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      // System.out.println("inAir");
      boolean inAir = cursor.isEmpty();
      context.getReturnBuffer().setTo(inAir);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private String encode(String text) {
    return text.replaceAll("\t", "    ");
  }

  private BlockPos toBlockPos(Object v) {
    if (!(v instanceof Table)) {
      throw new IllegalArgumentException(
          String.format("Table being a 3D vector (x,y,z) expected, but got %s!", v));
    }
    Table table = (Table) v;
    Object ox = table.rawget("x");
    Object oy = table.rawget("y");
    Object oz = table.rawget("z");
    int x = ((Number) ox).intValue();
    int y = ((Number) oy).intValue();
    int z = ((Number) oz).intValue();

    return new BlockPos(x, y, z);
  }

}
