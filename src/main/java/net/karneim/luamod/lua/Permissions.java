package net.karneim.luamod.lua;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static java.util.Arrays.asList;
import static net.karneim.luamod.lua.Permissions.AutoWizardPermission.OPERATOR;

import java.util.EnumSet;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import net.karneim.luamod.config.ModConfiguration;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.world.GameType;

public class Permissions {

  private static final AutoWizardPermission DEFAULT_AUT_WIZ_VALUE = OPERATOR;
  private static final String PERMISSIONS = "permissions";
  private static final String AUTO_WIZ_KEY = AutoWizardPermission.class.getSimpleName();

  private final ModConfiguration configuration;
  private AutoWizardPermission autoWizardPermission;

  public Permissions(ModConfiguration configuration) {
    this.configuration = checkNotNull(configuration);
  }

  public boolean isWizard(ICommandSender sender) {
    GameType playerGameMode = getGameMode(sender);
    AutoWizardPermission permission = getAutoWizardPermission();
    if (permission.isGranted(playerGameMode)) {
      return true;
    }
    if (permission == AutoWizardPermission.OPERATOR) {
      return isOperator(sender);
    }
    if (sender.getCommandSenderEntity() == null) {
      // Command block
      return true;
    }
    // TODO support white-listing of wizards
    return false;
  }

  private boolean isOperator(ICommandSender sender) {
    Entity e = sender.getCommandSenderEntity();
    if (e instanceof EntityPlayerMP) {
      EntityPlayerMP player = (EntityPlayerMP) e;
      UserListOpsEntry entry = (UserListOpsEntry) player.mcServer.getPlayerList().getOppedPlayers()
          .getEntry(player.getGameProfile());
      return entry != null;
    }
    if (e instanceof SpellEntity) {
      SpellEntity spell = (SpellEntity) e;
      return isOperator(spell.getOwner());
    }
    return false;
  }

  private GameType getGameMode(ICommandSender sender) {
    Entity e = sender.getCommandSenderEntity();
    if (e instanceof EntityPlayerMP) {
      EntityPlayerMP player = (EntityPlayerMP) e;
      return player.interactionManager.getGameType();
    }
    if (e instanceof SpellEntity) {
      SpellEntity spell = (SpellEntity) e;
      return getGameMode(spell.getOwner());
    }
    return GameType.NOT_SET;
  }

  public AutoWizardPermission getAutoWizardPermission() {
    if (autoWizardPermission == null) {
      String value =
          configuration.getStringOrNull(PERMISSIONS, AUTO_WIZ_KEY, DEFAULT_AUT_WIZ_VALUE.name());
      autoWizardPermission = AutoWizardPermission.valueOf(value);
      configuration.save();
    }
    return autoWizardPermission;
  }

  public void setAutoWizardPermission(AutoWizardPermission value) {
    this.autoWizardPermission = value;
    configuration.setString(PERMISSIONS, AUTO_WIZ_KEY, value.name());
    configuration.save();
  }

  enum AutoWizardPermission {
    OPERATOR(EnumSet.noneOf(GameType.class)), //
    CREATIVE(EnumSet.of(GameType.CREATIVE)), //
    SURVIVAL(EnumSet.of(GameType.SURVIVAL, GameType.CREATIVE)), //
    ALL(EnumSet.allOf(GameType.class)) //
    ;

    private final EnumSet<GameType> allowed;
    private static ImmutableList<String> names;

    static {
      names = ImmutableList
          .copyOf(transform(asList(values()), new Function<AutoWizardPermission, String>() {
            @Override
            public String apply(AutoWizardPermission input) {
              return input.name();
            }
          }));
    }

    private AutoWizardPermission(EnumSet<GameType> allowed) {
      this.allowed = allowed;
    }

    public boolean isGranted(GameType gameMode) {
      return allowed.contains(gameMode);
    }

    public static ImmutableList<String> names() {
      return names;
    };
  }


}
