package net.wizardsoflua.testenv.net;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface PacketDispatcherContext {
  public EntityPlayer getPlayerEntity(MessageContext ctx);

  public IThreadListener getThreadFromContext(MessageContext ctx);
}
