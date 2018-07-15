package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.event.SwingArmEvent;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.util.BasicLuaClass;
import net.wizardsoflua.lua.extension.util.LuaClassAttributes;

@AutoService(LuaConverter.class)
@LuaClassAttributes(name = SwingArmEventClass.NAME, superClass = EventClass.class)
@GenerateLuaClassTable(instance = SwingArmEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public class SwingArmEventClass
    extends BasicLuaClass<SwingArmEvent, SwingArmEventClass.Instance<SwingArmEvent>> {
  public static final String NAME = "SwingArmEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new SwingArmEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<SwingArmEvent>> toLuaInstance(SwingArmEvent javaInstance) {
    return new SwingArmEventClassInstanceTable<>(new Instance<>(javaInstance, getName(), injector),
        getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends SwingArmEvent> extends EventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    @LuaProperty
    public EnumHand getHand() {
      return delegate.getHand();
    }

    @LuaProperty
    public ItemStack getItem() {
      return delegate.getItemStack();
    }

    @LuaProperty
    public EntityPlayer getPlayer() {
      return delegate.getPlayer();
    }
  }
}
