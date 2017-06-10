package net.wizardsoflua.testenv.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class ClickAction extends ClientAction {

  private BlockPos pos;
  private EnumFacing face;

  public ClickAction() {}

  public ClickAction(BlockPos pos, EnumFacing face) {
    this.pos = pos;
    this.face = face;
  }

  @Override
  public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
    buffer.writeInt(pos.getX());
    buffer.writeInt(pos.getY());
    buffer.writeInt(pos.getZ());
    buffer.writeInt(face.ordinal());
  }

  @Override
  public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
    int x = buffer.readInt();
    int y = buffer.readInt();
    int z = buffer.readInt();
    pos = new BlockPos(x, y, z);
    int ord = buffer.readInt();
    face = EnumFacing.values()[ord];
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    System.out.println("pos: " + pos + ", " + face);
    Minecraft.getMinecraft().playerController.clickBlock(pos, face);
  }

}
