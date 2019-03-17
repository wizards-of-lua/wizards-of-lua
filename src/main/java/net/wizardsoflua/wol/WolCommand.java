package net.wizardsoflua.wol;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.wizardsoflua.wol.file.FileSection.PERSONAL;
import static net.wizardsoflua.wol.file.FileSection.SHARED;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.wol.browser.LoginCommand;
import net.wizardsoflua.wol.browser.LogoutCommand;
import net.wizardsoflua.wol.file.FileDeleteCommand;
import net.wizardsoflua.wol.file.FileEditCommand;
import net.wizardsoflua.wol.file.FileMoveCommand;
import net.wizardsoflua.wol.gist.GistGetCommand;
import net.wizardsoflua.wol.luatickslimit.PrintEventListenerLuaTicksLimitCommand;
import net.wizardsoflua.wol.luatickslimit.PrintLuaTicksLimitCommand;
import net.wizardsoflua.wol.luatickslimit.SetEventListenerLuaTicksLimitCommand;
import net.wizardsoflua.wol.luatickslimit.SetLuaTicksLimitCommand;
import net.wizardsoflua.wol.pack.PackExportCommand;
import net.wizardsoflua.wol.spell.SpellBreakCommand;
import net.wizardsoflua.wol.spell.SpellListCommand;

public class WolCommand {
  public static void register(CommandDispatcher<CommandSource> dispatcher, WizardsOfLua wol) {
    new WolCommand(wol).register(dispatcher);
  }

  private final WizardsOfLua wol;

  public WolCommand(WizardsOfLua wol) {
    this.wol = checkNotNull(wol, "wol == null!");
  }

  public void register(CommandDispatcher<CommandSource> dispatcher) {
    new LoginCommand(wol).register(dispatcher);
    new LogoutCommand(wol).register(dispatcher);
    new PrintEventListenerLuaTicksLimitCommand(wol).register(dispatcher);
    new SetEventListenerLuaTicksLimitCommand(wol).register(dispatcher);
    new PrintLuaTicksLimitCommand(wol).register(dispatcher);
    new SetLuaTicksLimitCommand(wol).register(dispatcher);
    new FileDeleteCommand(wol, PERSONAL).register(dispatcher);
    new FileEditCommand(wol, PERSONAL).register(dispatcher);
    new GistGetCommand(wol, PERSONAL).register(dispatcher);
    new FileMoveCommand(wol, PERSONAL).register(dispatcher);
    new FileDeleteCommand(wol, SHARED).register(dispatcher);
    new FileEditCommand(wol, SHARED).register(dispatcher);
    new GistGetCommand(wol, SHARED).register(dispatcher);
    new FileMoveCommand(wol, SHARED).register(dispatcher);
    new PackExportCommand(wol).register(dispatcher);
    new SpellListCommand(wol).register(dispatcher);
    new SpellBreakCommand(wol).register(dispatcher);
  }
}
