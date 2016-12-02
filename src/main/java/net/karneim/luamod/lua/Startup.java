package net.karneim.luamod.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URL;

import net.karneim.luamod.LuaMod;
import net.karneim.luamod.credentials.Credentials;
import net.karneim.luamod.credentials.Realm;
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
    String program = getProgram();
    if (program != null) {
      luaMod.logger.info("Running startup profile.");
      MinecraftServer server = checkNotNull(luaMod.getServer());
      ICommandSender sender = sender();
      ICommandSender owner = sender;

      World entityWorld = checkNotNull(server.getEntityWorld());
      SpellEntity spell = luaMod.getSpellEntityFactory().create(entityWorld, sender, owner);

      spell.setProgram(program);
      server.getEntityWorld().spawnEntityInWorld(spell);
    }
  }

  private String getProgram() throws IOException {
    URL url = luaMod.getProfiles().getStartupProfile();
    if (url == null) {
      return null;
    }
    Credentials credentials = luaMod.getCredentialsStore().retrieveCredentials(Realm.GitHub);
    String program = luaMod.getGistRepo().load(credentials, url);
    return program;
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
