package net.karneim.luamod.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URL;

import net.karneim.luamod.LuaMod;
import net.karneim.luamod.config.ModConfiguration;
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

  private static final String STARTUP_CAT = "startup";
  private static final String SPELLS_KEY = "spells";

  private final LuaMod luaMod;
  private final ModConfiguration configuration;
  private String spell;

  public Startup(LuaMod luaMod, ModConfiguration configuration) {
    this.luaMod = checkNotNull(luaMod);
    this.configuration = checkNotNull(configuration);
  }

  public String getSpell() {
    if (spell == null) {
      spell = configuration.getStringOrNull(SPELLS_KEY, STARTUP_CAT);
    }
    return spell;
  }

  public void setSpell(String spell) {
    this.spell = spell;
    configuration.setString(SPELLS_KEY, STARTUP_CAT, spell);
    configuration.save();
  }

  public void runStartupProfile() throws IOException, LoaderException {
    String requirements = getRequirements();
    String theSpell = getSpell();
    if (requirements != null && theSpell != null) {
      luaMod.logger.info("Running startup profile.");
      MinecraftServer server = checkNotNull(luaMod.getServer());
      ICommandSender sender = sender();
      ICommandSender owner = sender;

      World entityWorld = checkNotNull(server.getEntityWorld());
      SpellEntity spellEntity = luaMod.getSpellEntityFactory().create(entityWorld, sender, owner);
      spellEntity.setRequirements(requirements);
      spellEntity.setCommand(theSpell);
      server.getEntityWorld().spawnEntityInWorld(spellEntity);
    }
  }

  private String getRequirements() throws IOException {
    String refStr = luaMod.getProfiles().getStartupProfile();
    if (refStr == null) {
      refStr = luaMod.getProfiles().getDefaultProfile();
    }
    if (refStr == null) {
      return "";
    }
    return "require \"" + refStr + "\"";
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