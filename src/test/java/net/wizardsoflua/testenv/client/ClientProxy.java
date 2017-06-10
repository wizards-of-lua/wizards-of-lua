package net.wizardsoflua.testenv.client;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.wizardsoflua.testenv.CommonProxy;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

  private ClientSideEventHandler eventHandler = new ClientSideEventHandler();
  
  @Override
  public void onInit(FMLInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(eventHandler);
  }

}
