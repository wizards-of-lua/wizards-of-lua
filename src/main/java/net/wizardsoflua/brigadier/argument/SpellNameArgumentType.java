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

public class SpellNameArgumentType implements ArgumentType<String> {
  private final SpellRegistry registry;

  public SpellNameArgumentType(SpellRegistry registry) {
    this.registry = checkNotNull(registry, "registry == null!");
  }

  public static SpellNameArgumentType spellName() {
    return new SpellNameArgumentType(WizardsOfLua.instance.getSpellRegistry());
  }

  @Override
  public <S> String parse(StringReader reader) throws CommandSyntaxException {
    return reader.readString();
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
      SuggestionsBuilder builder) {
    return ISuggestionProvider.suggest(registry.getActiveNames(), builder);
  }

  @Override
  public String toString() {
    return "spellName()";
  }
}
