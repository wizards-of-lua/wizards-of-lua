package net.wizardsoflua.wol.spell;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraft.command.arguments.EntityArgument.singlePlayer;
import javax.inject.Inject;
import com.google.auto.service.AutoService;
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
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.extension.server.spi.CommandRegisterer;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.spell.SpellRegistry;

@AutoService(CommandRegisterer.class)
public class SpellBreakCommand implements CommandRegisterer, Command<CommandSource> {
  private static final String SID_ARGUMENT = "sid";
  private static final String NAME_ARGUMENT = "name";
  private static final String OWNER_ARGUMENT = "owner";
  @Inject
  private SpellRegistry spellRegistry;

  @Override
  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("wol")//
            .then(literal("spell")//
                .then(literal("break")//
                    .executes(this)//
                    .then(literal("all")//
                        .executes(this::breakAll)//
                    )//
                    .then(literal("bySid")//
                        .then(argument(SID_ARGUMENT, integer(0))//
                            .executes(this::breakBySid)//
                        ))//
                    .then(literal("byName")//
                        .then(argument(NAME_ARGUMENT, string())//
                            .executes(this::breakByName)//
                        ))//
                    .then(literal("byOwner")//
                        .then(argument(OWNER_ARGUMENT, singlePlayer())//
                            .executes(this::breakByOwner)//
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
    return breakSpells(source, spell -> entity.equals(spell.getOwner()));
  }

  public int breakAll(CommandContext<CommandSource> context) throws CommandSyntaxException {
    CommandSource source = context.getSource();
    Iterable<SpellEntity> spells = spellRegistry.getAll();
    return breakSpells(source, spells);
  }

  public int breakBySid(CommandContext<CommandSource> context) throws CommandSyntaxException {
    int sid = IntegerArgumentType.getInteger(context, SID_ARGUMENT);
    CommandSource source = context.getSource();
    return breakSpells(source, spell -> sid == spell.getSid());
  }

  public int breakByName(CommandContext<CommandSource> context) throws CommandSyntaxException {
    String name = StringArgumentType.getString(context, NAME_ARGUMENT);
    CommandSource source = context.getSource();
    return breakSpells(source, spell -> name.equals(spell.getName()));
  }

  public int breakByOwner(CommandContext<CommandSource> context) throws CommandSyntaxException {
    EntityPlayerMP owner = EntityArgument.getOnePlayer(context, OWNER_ARGUMENT);
    CommandSource source = context.getSource();
    return breakSpells(source, spell -> owner.equals(spell.getOwner()));
  }

  private int breakSpells(CommandSource source, Predicate<SpellEntity> predicate) {
    Iterable<SpellEntity> spells = spellRegistry.get(predicate);
    return breakSpells(source, spells);
  }

  private int breakSpells(CommandSource source, Iterable<SpellEntity> spells) {
    int count = 0;
    for (SpellEntity spell : spells) {
      spell.setDead();
      count++;
    }
    if (count == 1) {
      // TODO I18n
      source.sendFeedback(new WolAnnouncementMessage("Broke 1 spell"), true);
    } else if (count > 1) {
      // TODO I18n
      source.sendFeedback(new WolAnnouncementMessage("Broke " + count + " spells"), true);
    }
    return count;
  }


}
