package net.wizardsoflua.wol.spell;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraft.command.arguments.EntityArgument.singlePlayer;
import static net.wizardsoflua.brigadier.argument.SidArgumentType.sid;

import com.google.common.base.Predicate;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.brigadier.argument.LongArgumentType;
import net.wizardsoflua.brigadier.argument.SpellNameArgumentType;
import net.wizardsoflua.spell.SpellEntity;

public class SpellListCommand implements Command<CommandSource> {
  private static final String SID_ARGUMENT = "sid";
  private static final String NAME_ARGUMENT = "name";
  private static final String OWNER_ARGUMENT = "owner";

  private final WizardsOfLua wol;

  public SpellListCommand(WizardsOfLua wol) {
    this.wol = checkNotNull(wol, "wol == null!");
  }

  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("wol")//
            .then(literal("spell")//
                .then(literal("list")//
                    .executes(this)//
                    .then(literal("all")//
                        .executes(this::listAll)//
                    )//
                    .then(literal("bySid")//
                        .then(argument(SID_ARGUMENT, sid())//
                            .executes(this::listBySid)//
                        ))//
                    .then(literal("byName")//
                        .then(argument(NAME_ARGUMENT, SpellNameArgumentType.spellName())//
                            .executes(this::listByName)//
                        ))//
                    .then(literal("byOwner")//
                        .then(argument(OWNER_ARGUMENT, singlePlayer())//
                            .executes(this::listByOwner)//
                        ))//
                )));
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    CommandSource source = context.getSource();
    // TODO I18n
    String message = "Your active spells";
    return listSpells(source, message, spell -> source.equals(spell.getOwner()));
  }

  public int listAll(CommandContext<CommandSource> context) throws CommandSyntaxException {
    CommandSource source = context.getSource();
    // TODO I18n
    String message = "Active spells";
    Iterable<SpellEntity> spells = wol.getSpellRegistry().getAll();
    return listSpells(source, message, spells);
  }

  public int listBySid(CommandContext<CommandSource> context) throws CommandSyntaxException {
    long sid = LongArgumentType.getLong(context, SID_ARGUMENT);
    CommandSource source = context.getSource();
    // TODO I18n
    String message = "Active spells with sid " + sid;
    return listSpells(source, message, spell -> sid == spell.getSid());
  }

  public int listByName(CommandContext<CommandSource> context) throws CommandSyntaxException {
    String name = StringArgumentType.getString(context, NAME_ARGUMENT);
    CommandSource source = context.getSource();
    // TODO I18n
    String message = "Active spells with name '" + name + "'";
    return listSpells(source, message, spell -> name.equals(spell.getName()));
  }

  public int listByOwner(CommandContext<CommandSource> context) throws CommandSyntaxException {
    EntityPlayerMP owner = EntityArgument.getOnePlayer(context, OWNER_ARGUMENT);
    CommandSource source = context.getSource();
    // TODO I18n
    String message = "Active spells of " + owner.getName();
    return listSpells(source, message, spell -> owner.equals(spell.getOwner()));
  }

  private int listSpells(CommandSource source, String message, Predicate<SpellEntity> predicate) {
    Iterable<SpellEntity> spells = wol.getSpellRegistry().get(predicate);
    return listSpells(source, message, spells);
  }

  private int listSpells(CommandSource source, String message, Iterable<SpellEntity> spells) {
    source.sendFeedback(format(message, spells), true);
    return Command.SINGLE_SUCCESS; // FIXME: Return number of spells
  }

  private ITextComponent format(String message, Iterable<SpellEntity> spells) {
    WolAnnouncementMessage result = new WolAnnouncementMessage(message + ":\n");
    for (SpellEntity spell : spells) {
      TextComponentString name = new TextComponentString(spell.getSid() + ": ");
      name.setStyle(new Style().setColor(TextFormatting.DARK_GREEN));
      String description = spell.getProgram().getCode();
      int maxLength = 40;
      String ellipsis = "...";
      if (description.length() > maxLength + ellipsis.length()) {
        description = description.substring(0, maxLength) + ellipsis;
      }
      TextComponentString codeMsg = new TextComponentString(description + "\n");
      codeMsg.setStyle(new Style().setColor(TextFormatting.WHITE));
      result.appendSibling(name);
      result.appendSibling(codeMsg);
    }
    return result;
  }

}
