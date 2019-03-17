package net.wizardsoflua.brigadier.argument;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.command.ISuggestionProvider;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.spell.SpellRegistry;

public class SidArgumentType implements ArgumentType<Long> {
  private final SpellRegistry registry;

  private SidArgumentType(SpellRegistry registry) {
    this.registry = checkNotNull(registry, "registry == null!");
  }

  public static SidArgumentType sid() {
    return new SidArgumentType(WizardsOfLua.instance.getSpellRegistry());
  }

  public static long getSid(CommandContext<?> context, String name) {
    return context.getArgument(name, long.class);
  }

  @Override
  public <S> Long parse(StringReader reader) throws CommandSyntaxException {
    int start = reader.getCursor();
    while (reader.canRead() && StringReader.isAllowedNumber(reader.peek())) {
      reader.skip();
    }
    String number = reader.getString().substring(start, reader.getCursor());
    if (number.isEmpty()) {
      throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedInt()
          .createWithContext(reader);
    }
    try {
      return Long.parseLong(number);
    } catch (NumberFormatException ex) {
      reader.setCursor(start);
      throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(reader,
          number);
    }
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
      SuggestionsBuilder builder) {
    return ISuggestionProvider.suggest(registry.getActiveSids(), builder);
  }

  @Override
  public String toString() {
    return "sid()";
  }
}
