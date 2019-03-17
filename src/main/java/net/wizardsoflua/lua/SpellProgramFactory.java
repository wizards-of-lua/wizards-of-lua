package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.wizardsoflua.lua.module.print.PrintRedirector.PrintReceiver;

public class SpellProgramFactory {
  public interface Context extends SpellProgram.Context {
    String getSharedLuaPath();
  }

  private final Logger logger;
  private final Context context;

  public SpellProgramFactory(Logger logger, Context context) {
    this.logger = checkNotNull(logger, "logger==null!");
    this.context = checkNotNull(context, "context==null!");
  }

  public SpellProgram create(World world, CommandSource source, Entity owner,
      PrintReceiver spellLogger, String code, @Nullable String[] arguments) {
    String defaultLuaPath = getDefaultLuaPath(owner);
    return new SpellProgram(owner, code, arguments,  defaultLuaPath, world,
        spellLogger, context, logger);
  }

  private String getDefaultLuaPath(Entity owner) {
    if (owner instanceof EntityPlayer) {
      return context.getSharedLuaPath() + ";"
          + context.getLuaPathElementOfPlayer(owner.getCachedUniqueIdString());
    }
    return context.getSharedLuaPath();
  }
}
