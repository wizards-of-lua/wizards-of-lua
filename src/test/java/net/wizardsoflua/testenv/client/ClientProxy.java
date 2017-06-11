package net.wizardsoflua.testenv.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.wizardsoflua.testenv.CommonProxy;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

  private final Minecraft mc = Minecraft.getMinecraft();
  private final ClientSideEventHandler eventHandler = new ClientSideEventHandler();

  @Override
  public void onInit(FMLInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(eventHandler);
  }

  // In your client proxy:
  @Override
  public EntityPlayer getPlayerEntity(MessageContext ctx) {
    // Note that if you simply return 'Minecraft.getMinecraft().thePlayer',
    // your packets will not work because you will be getting a client
    // player even when you are on the server! Sounds absurd, but it's true.

    // Solution is to double-check side before returning the player:
    return (ctx.side.isClient() ? mc.player : super.getPlayerEntity(ctx));
  }

  @Override
  public IThreadListener getThreadFromContext(MessageContext ctx) {
    return (ctx.side.isClient() ? mc : super.getThreadFromContext(ctx));
  }
}
