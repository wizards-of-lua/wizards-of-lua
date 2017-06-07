package net.wizardsoflua.testenv;

import org.assertj.core.api.Assertions;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

public class TestDataFactory extends Assertions {

  protected ServerChatEvent newServerChatEvent(EntityPlayerMP player, String message) {
    ITextComponent component = new TextComponentString(message);
    return new ServerChatEvent(player, message, component);
  }

  protected RightClickBlock newRightClickBlockEvent(EntityPlayerMP player, BlockPos pos) {
    EnumHand hand = EnumHand.MAIN_HAND;
    EnumFacing face = EnumFacing.SOUTH;
    Vec3d hitVec = Vec3d.ZERO;
    return new RightClickBlock(player, hand, pos, face, hitVec);
  }

}
