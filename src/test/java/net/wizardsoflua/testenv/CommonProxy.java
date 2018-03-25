package net.wizardsoflua.testenv;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.wizardsoflua.testenv.net.PacketDispatcherContext;

public abstract class CommonProxy implements PacketDispatcherContext {
  public abstract void onInit(FMLInitializationEvent event);

  // In your server proxy (mine is named CommonProxy):
  /**
   * Returns a side-appropriate EntityPlayer for use during message handling
   */
  @Override
  public EntityPlayer getPlayerEntity(MessageContext ctx) {
    return ctx.getServerHandler().player;
  }

  /**
   * Returns the current thread based on side during message handling, used for ensuring that the
   * message is being handled by the main thread
   */
  @Override
  public IThreadListener getThreadFromContext(MessageContext ctx) {
    return ctx.getServerHandler().player.getServer();
  }

}
