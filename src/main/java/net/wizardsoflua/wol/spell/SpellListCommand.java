package net.wizardsoflua.wol.spell;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraft.command.arguments.EntityArgument.singlePlayer;
import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.YELLOW;
import com.google.common.base.Predicate;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.spell.SpellEntity;

public class SpellListCommand implements Command<CommandSource> {
  private static final int MAX_LINE_LENGTH = 55;
  private static final String ELLIPSIS = "\u2026";
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
                        .then(argument(SID_ARGUMENT, integer(0))//
                            .executes(this::listBySid)//
                        ))//
                    .then(literal("byName")//
                        .then(argument(NAME_ARGUMENT, string())//
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
    Entity entity = source.getEntity();
    if (entity == null) {
      // TODO I18n
      throw new CommandSyntaxException(null, new LiteralMessage(
          "Without further arguments this command can only be executed by an entity"));
    }
    // TODO I18n
    String message = "Your active spells";
    return listSpells(source, message, spell -> entity.equals(spell.getOwner()));
  }

  public int listAll(CommandContext<CommandSource> context) throws CommandSyntaxException {
    CommandSource source = context.getSource();
    // TODO I18n
    String message = "Active spells";
    Iterable<SpellEntity> spells = wol.getSpellRegistry().getAll();
    return listSpells(source, message, spells);
  }

  public int listBySid(CommandContext<CommandSource> context) throws CommandSyntaxException {
    int sid = IntegerArgumentType.getInteger(context, SID_ARGUMENT);
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
    String message = "Active spells of " + owner.getName().getString();
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
    ITextComponent result = WolAnnouncementMessage.createAnnouncement(message + ":\n");
    for (SpellEntity spell : spells) {
      ITextComponent line = new TextComponentString("") //
          .appendSibling(new TextComponentString("#" + spell.getSid()).applyTextStyle(YELLOW)) //
          .appendText(" ") //
          .appendSibling(spell.getDisplayName()) //
          .appendText(": ") //
      ;
      int maxCodeLength = MAX_LINE_LENGTH - line.getString().length();
      String code = spell.getProgram().getCode();
      if (code.length() > maxCodeLength) {
        if (maxCodeLength < ELLIPSIS.length()) {
          code = ELLIPSIS;
        } else {
          code = code.substring(0, maxCodeLength - ELLIPSIS.length()) + ELLIPSIS;
        }
      }
      line.appendSibling(new TextComponentString(code).applyTextStyle(AQUA));
      result.appendSibling(line).appendText("\n");
    }
    return result;
  }
}
