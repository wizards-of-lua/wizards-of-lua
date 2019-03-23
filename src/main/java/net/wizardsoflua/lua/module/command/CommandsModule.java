package net.wizardsoflua.lua.module.command;

import static net.minecraft.command.Commands.literal;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.extension.LuaTableExtension;
import net.wizardsoflua.lua.module.print.PrintRedirector.PrintReceiver;
import net.wizardsoflua.spell.SpellEntityFactory;

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

  // FIXME ensure this can be injected!
  @Resource
  private SpellEntityFactory spellEntityFactory;

  private final Set<String> customCommandNames = new HashSet<>();

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

    if (name.matches(".*\\s.*")) {
      throw new IllegalArgumentException(String.format(
          "Can't register command '%s'! Name must not contain any whitepace characters.", name));
    }

    if (existsCommand(name) && !isCustomCommand(name)) {
      throw new IllegalArgumentException(
          String.format("Can't register command '%s'! Command already exists.", name));
    }
    if (usage == null) {
      usage = "/" + name;
    }

    removeCommand(name);
    addCommand(name, luaCode, usage, permissionLevel);
  }

  /**
   * Deregisters the command with the given name. This function can only deregister custom commands
   * that have been registered with 'register'.
   */
  @LuaFunction
  public void deregister(String name) {
    if (existsCommand(name) && isCustomCommand(name)) {
      removeCommand(name);
    } else {
      throw new IllegalArgumentException(String.format("Can't deregister command '%s'", name));
    }
  }

  private boolean isCustomCommand(String name) {
    return customCommandNames.contains(name);
  }

  private boolean existsCommand(String name) {
    CommandDispatcher<CommandSource> dispatcher = server.getCommandManager().getDispatcher();
    RootCommandNode<CommandSource> root = dispatcher.getRoot();
    CommandNode<CommandSource> child = root.getChild(name);
    return child != null;
  }

  private void removeCommand(String name) {
    CommandDispatcher<CommandSource> dispatcher = server.getCommandManager().getDispatcher();
    RootCommandNode<CommandSource> root = dispatcher.getRoot();
    CommandNode<CommandSource> child = root.getChild(name);
    if (child != null) {
      root.getChildren().remove(child);
      try {
        Field fLiterals = CommandNode.class.getDeclaredField("literals");
        fLiterals.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, LiteralCommandNode<?>> literals =
            (Map<String, LiteralCommandNode<?>>) fLiterals.get(root);
        if (literals != null) {
          literals.remove(name);
        }
      } catch (NoSuchFieldException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
    refreshCommands();
  }

  private void addCommand(String name, String code, String usage, Integer permissionLevel) {
    CommandDispatcher<CommandSource> dispatcher = server.getCommandManager().getDispatcher();
    // FIXME how to implement the "usage"?
    // FIXME register command !!
    dispatcher.register(literal(name)
        .requires(src -> permissionLevel == null || src.hasPermissionLevel(permissionLevel))//
        .executes(context -> {
          CommandSource source = context.getSource();

          PrintReceiver printReceiver = new PrintReceiver() {
            @Override
            public void send(String message) {
              TextComponentString txt = new TextComponentString(message);
              source.sendFeedback(txt, true);
            }
          };

          // FIXME catch exceptions here? show them as error feedback?
          // return 0 on exception?
          spellEntityFactory.create(source, printReceiver, code);
          return Command.SINGLE_SUCCESS;
        }));
    refreshCommands();
  }

  private void refreshCommands() {
    for (EntityPlayerMP entityplayermp : server.getPlayerList().getPlayers()) {
      server.getCommandManager().sendCommandListPacket(entityplayermp);
    }
  }

}
