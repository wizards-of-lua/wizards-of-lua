package net.wizardsoflua.wol.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public abstract class MenuEntry {

  public abstract List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      Deque<String> argList, BlockPos targetPos);

  protected List<String> getMatchingTokens(@Nullable String actual, String... options) {
    return getMatchingTokens(actual, Lists.newArrayList(options));
  }

  protected List<String> getMatchingTokens(@Nullable String actual, Iterable<String> options) {
    List<String> result = new ArrayList<>();
    for (String option : options) {
      if (actual == null || option.startsWith(actual)) {
        result.add(option);
      }
    }
    Collections.sort(result);
    return result;
  }

}
