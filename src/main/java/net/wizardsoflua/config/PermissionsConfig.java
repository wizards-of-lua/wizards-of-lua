package net.wizardsoflua.config;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.world.GameType;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.table.TableUtils;

public class PermissionsConfig {

  private List<String> restrictedCommands;
  private List<WizardGameMode> wizardGameModes = Lists.newArrayList();
  private EnumSet<GameType> wizardGameModesEnumSet = EnumSet.noneOf(GameType.class);

  public PermissionsConfig() {
    restrictedCommands = Lists.newArrayList("debug", "defaultgamemode", "difficulty",
        "setworldspawn", "worldborder", "ban", "ban-ip", "deop", "kick", "op", "pardon",
        "pardon-ip", "save-all", "save-off", "save-on", "setidletimeout", "stop", "whitelist");
    Collections.sort(restrictedCommands);
  }

  public PermissionsConfig(Table table) {
    Iterable<String> restrictedCommandsIter =
        TableUtils.toJavaIterable(String.class, table.rawget("restrictedCommands"));
    restrictedCommands = Lists.newArrayList(restrictedCommandsIter);
    Iterable<WizardGameMode> wizardGameModesIter =
        TableUtils.toJavaIterable(WizardGameMode.class, table.rawget("wizardGameModes"));
    wizardGameModes = Lists.newArrayList(wizardGameModesIter);
    for (WizardGameMode wizardGameMode : wizardGameModes) {
      wizardGameModesEnumSet.addAll(wizardGameMode.getAllowed());
    }
  }

  public Table writeTo(Table table) {
    table.rawset("restrictedCommands", TableUtils.toLuaIterable(restrictedCommands));
    table.rawset("wizardGameModes", TableUtils.toLuaIterable(wizardGameModes));
    return table;
  }

  public List<String> getRestrictedCommands() {
    return restrictedCommands;
  }

  public EnumSet<GameType> getWizardGameModes() {
    return wizardGameModesEnumSet;
  }

}
