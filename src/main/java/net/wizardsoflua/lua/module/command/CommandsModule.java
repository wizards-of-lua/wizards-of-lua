package net.wizardsoflua.lua.module.command;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.auto.service.AutoService;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.LuaCommand;
import net.wizardsoflua.lua.extension.LuaTableExtension;
import net.wizardsoflua.reflect.Fields;

/**
 * The <span class="notranslate">Commands</span> module allows the registration of custom commands
 * than can be used from the chat line.
 */
@AutoService(SpellExtension.class)
@GenerateLuaModuleTable
@GenerateLuaDoc(name = CommandsModule.NAME, subtitle = "Managing Custom Commands")
public class CommandsModule extends LuaTableExtension {
  public static final String NAME = "Commands";
  @Resource
  private LuaConverters converters;
  @Resource
  private MinecraftServer server;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table getTable() {
    return new CommandsModuleTable<>(this, converters);
  }

  /**
   * Registers a new custom command with the given name and the given Lua code (provided as text).
   * Optionally accepts the usage string as a third parameter and the command's permission level as
   * a fourth parameter.
   *
   * The Lua code will be compiled every time when the command is issued.
   *
   * The command stays registered until it is deregistered or the server is being restarted.
   *
   * #### Permission Level
   *
   * Set the permission level to <tt>nil</tt> if you want that all players can use the command (this
   * is the default). Set the permission level to a value between 1 and 4 if you want only operators
   * to use it. See section "op-permission-level" of the [Minecraft
   * Wiki](https://minecraft.gamepedia.com/Server.properties) for more information about the meaning
   * of the different permission levels.
   *
   * #### Example
   *
   * Registering a command called <span class="notranslate">"home"</span> that teleports the calling
   * player to his last known spawn point, or otherwise to the world spawn.
   *
   * <code>
   * Commands.register("home",[[
   *   local p = spell.owner
   *   local n = p.nbt
   *   if n.SpawnX then
   *      p.pos = Vec3(n.SpawnX, n.SpawnY, n.SpawnZ)
   *   else
   *      p.pos = p.world.spawnPoint
   *   end
   *   ]])
   * </code>
   *
   * #### Example
   *
   * Registering a command called <span class="notranslate">"health"</span> that can set the health
   * of the specified player to the specified value.
   *
   * <code>
   * Commands.register("health",[[
   *   local name,value = ...
   *   local p=Entities.find("@a[name="..name.."]")[1]
   *   if p then
   *     p.health=tonumber(value)
   *   else
   *     print("player not found")
   *   end
   * ]], "/health <player> <new health>")
   * </code>
   *
   * #### Example
   *
   * Registering a command that needs operator permission level 4.
   *
   * <code>
   * Commands.register("cool-command",[[
   *   print("you are very cool")
   * ]], "/cool-command", 4)
   * </code>
   *
   */
  @LuaFunction
  public void register(String name, String luaCode, @Nullable String usage,
      @Nullable Integer permissionLevel) {
    CommandHandler ch = (CommandHandler) server.getCommandManager();
    ICommand old = ch.getCommands().get(name);
    if (name.matches(".*\\s.*")) {
      throw new IllegalArgumentException(String.format(
          "Can't register command '%s'! Name must not contain any whitepace characters.", name));
    }

    if (old != null && !(old instanceof CustomCommand)) {
      throw new IllegalArgumentException(
          String.format("Can't register command '%s'! Command already exists.", name));
    }
    if (usage == null) {
      usage = "/" + name;
    }
    removeCommand(ch, name);
    ICommand cmd = new CustomCommand(name, luaCode, usage, permissionLevel);
    ch.registerCommand(cmd);
  }

  /**
   * Deregisters the command with the given name. This function can only deregister custom commands
   * that have been registered with 'register'.
   */
  @LuaFunction
  public void deregister(String name) {
    CommandHandler ch = (CommandHandler) server.getCommandManager();
    ICommand cmd = ch.getCommands().get(name);
    if (cmd instanceof CustomCommand) {
      removeCommand(ch, name);
    } else {
      throw new IllegalArgumentException(String.format("Can't deregister command '%s'", name));
    }
  }

  private void removeCommand(CommandHandler ch, String name) {
    ICommand cmd = ch.getCommands().remove(name);
    if (cmd != null) {
      Set<ICommand> set = Fields.getCommandSet(ch);
      set.remove(cmd);
    }
  }

  private static class CustomCommand implements ICommand {
    private final String name;
    private final String luaCode;
    private final String usage;
    private final @Nullable Integer permissionLevel;

    public CustomCommand(String name, String luaCode, String usage,
        @Nullable Integer permissionLevel) {
      this.name = checkNotNull(name, "name==null!").trim();
      this.luaCode = checkNotNull(luaCode, "luaCode==null!").trim();
      this.usage = checkNotNull(usage, "usage==null!");
      this.permissionLevel = permissionLevel;
    }

    @Override
    public int compareTo(ICommand o) {
      return name.compareTo(o.getName());
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public String getUsage(ICommandSender sender) {
      return usage;
    }

    @Override
    public List<String> getAliases() {
      return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
        throws CommandException {
      LuaCommand luaCommand = (LuaCommand) server.getCommandManager().getCommands().get("lua");
      Object[] objArgs = Arrays.copyOf(args, args.length, Object[].class);
      luaCommand.execute(server, sender, luaCode, objArgs);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
      if (permissionLevel != null) {
        return sender.canUseCommand(permissionLevel, name);
      } else {
        return true;
      }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
        String[] args, BlockPos targetPos) {
      return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
      return false;
    }

  }

}
