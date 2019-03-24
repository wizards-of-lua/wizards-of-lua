package net.wizardsoflua.command;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.minecraft.command.Commands.literal;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.wizardsoflua.lua.module.print.PrintRedirector.PrintReceiver;
import net.wizardsoflua.spell.SpellEntityFactory;

public class CustomCommandRegistry {
  private final Set<String> registeredNames = new HashSet<>();

  private final MinecraftServer server;
  private final SpellEntityFactory spellEntityFactory;

  public CustomCommandRegistry(MinecraftServer server, SpellEntityFactory spellEntityFactory) {
    this.server = checkNotNull(server, "server == null!");
    this.spellEntityFactory = checkNotNull(spellEntityFactory, "spellEntityFactory == null!");
  }

  public void addCommand(String name, String code, String usage, Integer permissionLevel) {
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
    registeredNames.add(name);
    refreshCommands();
  }

  public boolean isCustomCommand(String name) {
    return registeredNames.contains(name);
  }

  public boolean existsCommand(String name) {
    CommandDispatcher<CommandSource> dispatcher = server.getCommandManager().getDispatcher();
    RootCommandNode<CommandSource> root = dispatcher.getRoot();
    CommandNode<CommandSource> child = root.getChild(name);
    return child != null;
  }

  public void removeCommand(String name) {
    CommandDispatcher<CommandSource> dispatcher = server.getCommandManager().getDispatcher();
    RootCommandNode<CommandSource> root = dispatcher.getRoot();
    CommandNode<CommandSource> child = root.getChild(name);
    if (child != null) {
      registeredNames.remove(name);
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

  public void refreshCommands() {
    for (EntityPlayerMP entityplayermp : server.getPlayerList().getPlayers()) {
      server.getCommandManager().sendCommandListPacket(entityplayermp);
    }
  }
}
