package net.karneim.luamod.lua.classes.entity.player;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.karneim.luamod.lua.util.LuaPreconditions.checkTypeString;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.sandius.rembulan.Table;

@LuaModule("PlayerMP")
public class EntityPlayerMPClass extends DelegatingLuaClass<EntityPlayerMP> {
  public EntityPlayerMPClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends EntityPlayerMP> b,
      EntityPlayerMP delegate) {
    EntityPlayerMPWrapper d = new EntityPlayerMPWrapper(delegate);
    b.add("gamemode", () -> repo.wrap(delegate.interactionManager.getGameType()), d::setGameMode);
  }

  private static class EntityPlayerMPWrapper {
    private final EntityPlayer delegate;

    public EntityPlayerMPWrapper(EntityPlayer delegate) {
      this.delegate = checkNotNull(delegate, "delegate == null!");
    }

    private void setGameMode(Object arg) {
      GameType mode = GameType.valueOf(checkTypeString(arg));
      delegate.setGameType(mode);
    }
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
