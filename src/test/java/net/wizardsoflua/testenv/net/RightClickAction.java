package net.wizardsoflua.testenv.net;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class RightClickAction extends ClientAction {

  private BlockPos pos;
  private EnumFacing face;
  private Vec3d vec;

  public RightClickAction() {}

  public RightClickAction(BlockPos pos, EnumFacing face, Vec3d vec) {
    this.pos = pos;
    this.face = face;
    this.vec = vec;
  }

  @Override
  protected void read(PacketBuffer buffer) throws IOException {
    int x = buffer.readInt();
    int y = buffer.readInt();
    int z = buffer.readInt();
    pos = new BlockPos(x, y, z);
    int ordFacing = buffer.readInt();
    face = EnumFacing.values()[ordFacing];
    double vx = buffer.readDouble();
    double vy = buffer.readDouble();
    double vz = buffer.readDouble();
    vec = new Vec3d(vx, vy, vz);
  }

  @Override
  protected void write(PacketBuffer buffer) throws IOException {
    buffer.writeInt(pos.getX());
    buffer.writeInt(pos.getY());
    buffer.writeInt(pos.getZ());
    buffer.writeInt(face.ordinal());
    buffer.writeDouble(vec.xCoord);
    buffer.writeDouble(vec.yCoord);
    buffer.writeDouble(vec.zCoord);
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    System.out.println("right-click at pos: " + pos + ", " + face);

    Minecraft.getMinecraft().objectMouseOver = new RayTraceResult(vec, face, pos);
    try {
      Method m = Minecraft.class.getDeclaredMethod("rightClickMouse");
      m.setAccessible(true);
      m.invoke(Minecraft.getMinecraft());
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

}
