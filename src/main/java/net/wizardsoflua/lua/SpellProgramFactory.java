package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.wizardsoflua.lua.dependency.ModuleDependencies;
import net.wizardsoflua.lua.dependency.ModuleDependency;
import net.wizardsoflua.lua.module.system.SystemAdapter;
import net.wizardsoflua.profiles.Profiles;

public class SpellProgramFactory {

  public interface Context extends SpellProgram.Context {
    Profiles getProfiles();

    String getSharedLuaPath();

    File getScriptDir();

    long getScriptTimeoutMillis();

    boolean isScriptGatewayEnabled();
  }

  private final Context context;

  public SpellProgramFactory(Context context) {
    this.context = checkNotNull(context, "context==null!");
  }

  public SpellProgram create(World world, ICommandSender owner, String code) {
    ModuleDependencies dependencies = createDependencies(owner);
    String defaultLuaPath = getDefaultLuaPath(owner);
    SystemAdapter systemAdapter = createSystemAdapter();
    return new SpellProgram(owner, code, dependencies, defaultLuaPath, world, systemAdapter,
        context);
  }

  private String getDefaultLuaPath(ICommandSender owner) {
    Entity entity = owner.getCommandSenderEntity();
    if (entity instanceof EntityPlayer) {
      return context.getSharedLuaPath() + ";"
          + context.getLuaPathElementOfPlayer(entity.getCachedUniqueIdString());
    }
    return context.getSharedLuaPath();
  }

  private SystemAdapter createSystemAdapter() {
    return new SystemAdapter(context.isScriptGatewayEnabled(), context.getScriptTimeoutMillis(),
        context.getScriptDir());
  }

  private ModuleDependencies createDependencies(ICommandSender owner) {
    ModuleDependencies result = new ModuleDependencies();
    result.add(new ModuleDependency("wol.Globals"));
    result.add(new ModuleDependency("wol.inspect"));
    result.add(new ModuleDependency("wol.Check"));
    result.add(new ModuleDependency("wol.Object"));
    result.add(new ModuleDependency("wol.Vec3"));
    result.add(new ModuleDependency("wol.Material"));
    result.add(new ModuleDependency("wol.Block"));
    result.add(new ModuleDependency("wol.Entity"));
    result.add(new ModuleDependency("wol.Spell"));
    result.add(new ModuleDependency("wol.Player"));

    String sharedProfile = context.getProfiles().getSharedProfile();
    if (sharedProfile != null) {
      result.add(new ModuleDependency(sharedProfile));
    }
    Entity entity = owner.getCommandSenderEntity();
    if (entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) entity;
      String playerProfile = context.getProfiles().getProfile(player);
      if (playerProfile != null) {
        result.add(new ModuleDependency(playerProfile));
      }
    }
    return result;
  }
}
