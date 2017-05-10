package net.karneim.luamod.lua.classes.tileentity;

import static net.karneim.luamod.lua.util.LuaPreconditions.checkType;
import static net.karneim.luamod.lua.util.LuaPreconditions.checkTypeString;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.text.TextComponentString;
import net.sandius.rembulan.Table;

@LuaModule("CommandBlock")
public class CommandBlockClass extends DelegatingLuaClass<CommandBlockBaseLogic> {
  public CommandBlockClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends CommandBlockBaseLogic> b,
      CommandBlockBaseLogic d) {
    b.add("command", () -> repo.wrap(d.getCommand()), o -> d.setCommand(checkTypeString(o)));
    b.addReadOnly("commandBlockType", () -> repo.wrap(d.getCommandBlockType()));
    b.add("lastOutput", () -> repo.wrap(d.getLastOutput()),
        o -> d.setLastOutput(new TextComponentString(checkTypeString(o))));
    b.add("name", () -> repo.wrap(d.getName()), o -> d.setName(checkTypeString(o)));
    b.addReadOnly("position", () -> repo.wrap(d.getPositionVector()));
    b.add("successCount", () -> repo.wrap(d.getSuccessCount()),
        o -> d.setSuccessCount(checkType(o, Number.class).intValue()));
    b.add("successCount", () -> repo.wrap(d.shouldTrackOutput()),
        o -> d.setTrackOutput(checkType(o, Boolean.class)));
    b.addReadOnly("world", () -> repo.wrap(d.getEntityWorld()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
