package net.karneim.luamod.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URL;

import net.karneim.luamod.LuaMod;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.sandius.rembulan.load.LoaderException;

public class Startup {

  private final LuaMod luaMod;

  public Startup(LuaMod luaMod) {
    this.luaMod = checkNotNull(luaMod);
  }

  public void runStartupProfile() throws IOException, LoaderException {
    String requirements = getRequirements();
    if (requirements != null) {
      luaMod.logger.info("Running startup profile.");
      MinecraftServer server = checkNotNull(luaMod.getServer());
      ICommandSender sender = sender();
      ICommandSender owner = sender;

      World entityWorld = checkNotNull(server.getEntityWorld());
      SpellEntity spell = luaMod.getSpellEntityFactory().create(entityWorld, sender, owner);

      spell.setRequirements(requirements);
      spell.setCommand("-- startup");
      server.getEntityWorld().spawnEntityInWorld(spell);
    }
  }

  private String getRequirements() throws IOException {
    URL url = luaMod.getProfiles().getStartupProfile();
    if (url == null) {
      return "";
    }
    return "require \"" + url.toExternalForm() + "\"";
  }

  private ICommandSender sender() {
    return new ICommandSender() {
      public String getName() {
        return "Startup";
      }

      public ITextComponent getDisplayName() {
        return new TextComponentString(this.getName());
      }

      public void addChatMessage(ITextComponent component) {}

      public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
        return permLevel <= 2;
      }

      public BlockPos getPosition() {
        return BlockPos.ORIGIN;
      }

      public Vec3d getPositionVector() {
        return Vec3d.ZERO;
      }

      public World getEntityWorld() {
        return luaMod.getServer().getEntityWorld();
      }

      public Entity getCommandSenderEntity() {
        return null;
      }

      public boolean sendCommandFeedback() {
        return false;
      }

      public void setCommandStat(CommandResultStats.Type type, int amount) {}

      public MinecraftServer getServer() {
        return luaMod.getServer();
      }
    };
  }

}
