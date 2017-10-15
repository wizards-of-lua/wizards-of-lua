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

public class LeftClickAction extends ClientAction {

  private BlockPos pos;
  private EnumFacing face;

  public LeftClickAction() {}

  public LeftClickAction(BlockPos pos, EnumFacing face) {
    this.pos = pos;
    this.face = face;
  }

  @Override
  protected void read(PacketBuffer buffer) throws IOException {
    int x = buffer.readInt();
    int y = buffer.readInt();
    int z = buffer.readInt();
    pos = new BlockPos(x, y, z);
    int ord = buffer.readInt();
    face = EnumFacing.values()[ord];
  }

  @Override
  protected void write(PacketBuffer buffer) throws IOException {
    buffer.writeInt(pos.getX());
    buffer.writeInt(pos.getY());
    buffer.writeInt(pos.getZ());
    buffer.writeInt(face.ordinal());
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    System.out.println("left-click at pos: " + pos + ", " + face);
    
    Vec3d vec = new Vec3d(pos);
    
    Minecraft.getMinecraft().objectMouseOver = new RayTraceResult(vec, face, pos);
    try {
      Method m = Minecraft.class.getDeclaredMethod("clickMouse");
      m.setAccessible(true);
      m.invoke(Minecraft.getMinecraft());
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      throw new UndeclaredThrowableException(e);
    }
    
  }

}
