package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.common.Delegator;

@AutoService(LuaConverter.class)
@LuaClassAttributes(name = PlayerInteractEventClass.NAME, superClass = EventClass.class)
@GenerateLuaClassTable(instance = PlayerInteractEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public class PlayerInteractEventClass extends
    BasicLuaClass<PlayerInteractEvent, PlayerInteractEventClass.Instance<PlayerInteractEvent>> {
  public static final String NAME = "PlayerInteractEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new PlayerInteractEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<PlayerInteractEvent>> toLuaInstance(
      PlayerInteractEvent javaInstance) {
    return new PlayerInteractEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends PlayerInteractEvent> extends EventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    @LuaProperty
    public EntityPlayer getPlayer() {
      return delegate.getEntityPlayer();
    }

    @LuaProperty
    public EnumFacing getFace() {
      return delegate.getFace();
    }

    @LuaProperty
    public EnumHand getHand() {
      return delegate.getHand();
    }

    @LuaProperty
    public BlockPos getPos() {
      return delegate.getPos();
    }

    @LuaProperty
    public ItemStack getItem() {
      return delegate.getItemStack();
    }
  }
}
