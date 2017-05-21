package net.wizardsoflua;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;

@Mod(modid = WizardsOfLua.MODID, version = WizardsOfLua.VERSION, acceptableRemoteVersions = "*")
public class WizardsOfLua {
  public static final String MODID = "wol";
  public static final String VERSION = "0.0.0";

  @EventHandler
  public void init(FMLInitializationEvent event) {}

  @EventHandler
  public void serverStarted(FMLServerStartedEvent event) {
    System.out.println("serverStarted");
  }

}
