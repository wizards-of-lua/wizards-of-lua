package net.wizardsoflua.command;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.server.api.ServerScoped;
import net.wizardsoflua.lua.module.print.PrintRedirector.PrintReceiver;
import net.wizardsoflua.spell.SpellEntityFactory;

@ServerScoped
public class CustomCommandRegistry {
  private static final String ARGUMENTS_ARGUMENT = "arguments";
  @Resource
  private MinecraftServer server;
  @Inject
  private SpellEntityFactory spellEntityFactory;
  private final Set<String> registeredNames = new HashSet<>();

  public void addCommand(String name, String code, String usage, Integer permissionLevel) {
    CommandDispatcher<CommandSource> dispatcher = server.getCommandManager().getDispatcher();
    // FIXME how to implement the "usage"?
    // FIXME register command !!
    dispatcher.register(literal(name)
        .requires(src -> permissionLevel == null || src.hasPermissionLevel(permissionLevel)) //
        .executes(context -> run(context, code)) //
        .then(argument(ARGUMENTS_ARGUMENT, greedyString()) //
            .executes(context -> {
              String arguments = StringArgumentType.getString(context, ARGUMENTS_ARGUMENT);
              Iterable<String> iterable = Splitter.on(' ').split(arguments);
              String[] args = Iterables.toArray(iterable, String.class);
              return run(context, code, args);
            }) //
        ));
    registeredNames.add(name);
    refreshCommands();
  }

  private int run(CommandContext<CommandSource> context, String code, String... arguments) {
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
    spellEntityFactory.create(source, printReceiver, code, arguments);
    return Command.SINGLE_SUCCESS;
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
