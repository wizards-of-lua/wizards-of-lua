package net.wizardsoflua;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = WizardsOfLua.MODID, version = WizardsOfLua.VERSION)
public class WizardsOfLua {
  public static final String MODID = "wol";
  public static final String VERSION = "0.0.0";

  @EventHandler
  public void init(FMLInitializationEvent event) {
    // some example code
    System.out.println("DIRT BLOCK >> " + Blocks.DIRT.getUnlocalizedName());
    //throw new Error();
  }
}
