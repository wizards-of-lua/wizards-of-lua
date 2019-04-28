package net.wizardsoflua;

import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.wizardsoflua.config.WolConfig;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.file.LuaFileRepository;
import net.wizardsoflua.gist.GistRepo;
import net.wizardsoflua.spell.SpellEntityFactory;
import net.wizardsoflua.spell.SpellRegistry;

@ServerScoped
public class WolServer {
  @Resource
  private WizardsOfLua wol;
  @Resource
  private MinecraftServer server;

  public WolServer() {


  }

  public WolConfig getConfig() {
    // TODO Auto-generated method stub
    return null;
  }

  public GistRepo getGistRepo() {
    // TODO Auto-generated method stub
    return null;
  }

  public LuaFileRepository getFileRepository() {
    // TODO Auto-generated method stub
    return null;
  }

  public SpellEntityFactory getSpellEntityFactory() {
    // TODO Auto-generated method stub
    return null;
  }

  public SpellRegistry getSpellRegistry() {
    // TODO Auto-generated method stub
    return null;
  }

  public void runStartupSequence(CommandSource source) {
    // TODO Auto-generated method stub

  }
}
