package net.karneim.luamod.lua.classes;

import static com.google.common.base.Preconditions.checkNotNull;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.entity.EntityLivingBase;
import net.sandius.rembulan.Table;

@LuaModule("EntityLivingBase")
public class EntityLivingBaseClass extends DelegatingLuaClass<EntityLivingBase> {
  public EntityLivingBaseClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends EntityLivingBase> b,
      EntityLivingBase delegate) {
    EntityLivingBaseWrapper d = new EntityLivingBaseWrapper(delegate);
    b.add("armor", () -> repo.wrap(delegate.getArmorInventoryList()), null);
    b.add("mainHand", () -> repo.wrap(delegate.getHeldItemMainhand()), null);
    b.add("offHand", () -> repo.wrap(delegate.getHeldItemOffhand()), null);
    b.add("health", delegate::getHealth, d::setHealth);
  }

  private static class EntityLivingBaseWrapper {
    private final EntityLivingBase delegate;

    public EntityLivingBaseWrapper(EntityLivingBase delegate) {
      this.delegate = checkNotNull(delegate, "delegate == null!");
    }

    private void setHealth(Object arg) {
      float value = ((Number) arg).floatValue();
      delegate.setHealth(value);
    }
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
