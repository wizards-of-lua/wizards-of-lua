package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

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

  public SpellProgram create(World world, ICommandSender owner, String code,
      @Nullable String[] arguments) {
    String defaultLuaPath = getDefaultLuaPath(owner);
    return new SpellProgram(owner, code, arguments, defaultLuaPath, world, context, logger);
  }

  private String getDefaultLuaPath(ICommandSender owner) {
    Entity entity = owner.getCommandSenderEntity();
    if (entity instanceof EntityPlayer) {
      return context.getSharedLuaPath() + ";"
          + context.getLuaPathElementOfPlayer(entity.getCachedUniqueIdString());
    }
    return context.getSharedLuaPath();
  }
}
