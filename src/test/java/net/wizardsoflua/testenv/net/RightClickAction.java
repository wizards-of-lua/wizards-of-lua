package net.wizardsoflua.testenv.net;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RightClickAction extends ClientAction {

  private BlockPos pos;
  private EnumFacing face;
  private Vec3d vec;
  private EnumHand hand;

  public RightClickAction() {}

  public RightClickAction(BlockPos pos, EnumFacing face, Vec3d vec, EnumHand hand) {
    this.pos = pos;
    this.face = face;
    this.vec = vec;
    this.hand = hand;
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
    int ordHand = buffer.readInt();
    hand = EnumHand.values()[ordHand];
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
    buffer.writeInt(hand.ordinal());
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    System.out.println("right-click at pos: " + pos + ", " + face);
    EntityPlayerSP playerSp = (EntityPlayerSP) player;
    WorldClient world = (WorldClient) playerSp.getEntityWorld();
    Minecraft.getMinecraft().playerController.processRightClickBlock(playerSp, world, pos, face,
        vec, hand);
  }

}
