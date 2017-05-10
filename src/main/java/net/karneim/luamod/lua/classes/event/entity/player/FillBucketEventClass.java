package net.karneim.luamod.lua.classes.event.entity.player;

import static net.karneim.luamod.lua.util.LuaPreconditions.checkTypeDelegatingTable;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.sandius.rembulan.Table;

@LuaModule("FillBucketEvent")
public class FillBucketEventClass extends DelegatingLuaClass<FillBucketEvent> {
  public FillBucketEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends FillBucketEvent> b, FillBucketEvent d) {
    b.addReadOnly("emptyBucket", () -> repo.wrap(d.getEmptyBucket()));
    b.add("filledBucket", () -> repo.wrap(d.getFilledBucket()),
        o -> d.setFilledBucket(checkTypeDelegatingTable(o, ItemStack.class)));
    b.addReadOnly("target", () -> repo.wrap(d.getTarget()));
    b.addReadOnly("world", () -> repo.wrap(d.getWorld()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
