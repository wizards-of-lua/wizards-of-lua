package net.wizardsoflua.testenv;

import static java.util.Objects.requireNonNull;
import static net.wizardsoflua.testenv.GameRuleDsl.BooleanRule.doDaylightCycle;
import static net.wizardsoflua.testenv.GameRuleDsl.BooleanRule.doMobSpawning;
import static net.wizardsoflua.testenv.GameRuleDsl.BooleanRule.logAdminCommands;
import static net.wizardsoflua.testenv.GameRuleDsl.BooleanRule.sendCommandFeedback;
import java.util.EnumMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

public class GameRuleDsl {
  public enum BooleanRule {
    doDaylightCycle, //
    doMobSpawning, //
    logAdminCommands, //
    sendCommandFeedback, //
    ;
  }

  private final EnumMap<BooleanRule, Boolean> booleanRuleCache = new EnumMap<>(BooleanRule.class);
  private final MinecraftServer server;

  public GameRuleDsl(MinecraftServer server) {
    this.server = requireNonNull(server, "server");
  }

  void saveState() {
    for (BooleanRule key : BooleanRule.values()) {
      boolean value = getBoolean(key);
      booleanRuleCache.put(key, value);
    }
  }

  public void restoreState() {
    for (BooleanRule key : BooleanRule.values()) {
      Boolean value = booleanRuleCache.get(key);
      if (value != null) {
        setBoolean(key, value);
      }
    }
  }

  private GameRules getGameRules() {
    return server.getGameRules();
  }

  private boolean getBoolean(BooleanRule key) {
    return getGameRules().getBoolean(key.name());
  }

  private void setBoolean(BooleanRule key, boolean value) {
    getGameRules().setOrCreateGameRule(key.name(), String.valueOf(value), server);
  }

  public boolean getDoDaylighCycle() {
    return getBoolean(doDaylightCycle);
  }

  public void setDoDaylightCycle(boolean value) {
    setBoolean(doDaylightCycle, value);
  }

  public boolean getDoMobSpawning() {
    return getBoolean(doMobSpawning);
  }

  public void setDoMobSpawning(boolean value) {
    setBoolean(doMobSpawning, value);
  }

  public boolean getLogAdminCommands() {
    return getBoolean(logAdminCommands);
  }

  public void setLogAdminCommands(boolean value) {
    setBoolean(logAdminCommands, value);
  }

  public boolean getSendCommandFeedback() {
    return getBoolean(sendCommandFeedback);
  }

  public void setSendCommandFeedback(boolean value) {
    setBoolean(sendCommandFeedback, value);
  }
}
