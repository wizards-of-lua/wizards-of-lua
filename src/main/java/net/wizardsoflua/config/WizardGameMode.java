package net.wizardsoflua.config;

import static com.google.common.collect.Iterables.transform;
import static java.util.Arrays.asList;

import java.util.EnumSet;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import net.minecraft.world.GameType;

public enum WizardGameMode {
  CREATIVE(EnumSet.of(GameType.CREATIVE)), //
  SURVIVAL(EnumSet.of(GameType.SURVIVAL)), //
  ADVENTURE(EnumSet.of(GameType.ADVENTURE)), //
  SPECTATOR(EnumSet.of(GameType.SPECTATOR)), //
  ALL(EnumSet.allOf(GameType.class)) //
  ;

  private final EnumSet<GameType> allowed;
  private static ImmutableList<String> names;

  static {
    names =
        ImmutableList.copyOf(transform(asList(values()), new Function<WizardGameMode, String>() {
          @Override
          public String apply(WizardGameMode input) {
            return input.name();
          }
        }));
  }

  private WizardGameMode(EnumSet<GameType> allowed) {
    this.allowed = allowed;
  }

  public EnumSet<GameType> getAllowed() {
    return allowed;
  }

  public static ImmutableList<String> names() {
    return names;
  };
}
