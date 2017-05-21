package net.wizardsoflua.testenv;

import java.util.List;
import java.util.Map;

import net.minecraftforge.gradle.GradleStartCommon;

public class MinecraftStarter extends GradleStartCommon {

  private static MinecraftStarter starter = null;

  public static void start() throws Throwable {
    if (starter == null) {
      starter = new MinecraftStarter();
      starter.launch(new String[] {"-DFORGE_FORCE_FRAME_RECALC=true"});
    }
  }

  @Override
  protected String getTweakClass() {
    return "net.minecraftforge.fml.common.launcher.FMLServerTweaker";
  }

  @Override
  protected String getBounceClass() {
    return "net.minecraft.launchwrapper.Launch";
  }

  @Override
  protected void preLaunch(Map<String, String> argMap, List<String> extras) {}

  @Override
  protected void setDefaultArguments(Map<String, String> argMap) {}

}
