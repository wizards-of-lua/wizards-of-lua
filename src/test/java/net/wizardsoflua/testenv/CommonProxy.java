package net.wizardsoflua.testenv;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class CommonProxy {
  public abstract void onInit(FMLInitializationEvent event);

  // In your server proxy (mine is named CommonProxy):
  /**
   * Returns a side-appropriate EntityPlayer for use during message handling
   */
  public EntityPlayer getPlayerEntity(MessageContext ctx) {
    return ctx.getServerHandler().playerEntity;
  }

  /**
   * Returns the current thread based on side during message handling, used for ensuring that the
   * message is being handled by the main thread
   */
  public IThreadListener getThreadFromContext(MessageContext ctx) {
    return ctx.getServerHandler().playerEntity.getServer();
  }

}
