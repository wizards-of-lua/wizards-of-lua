package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.util.BasicLuaClass;
import net.wizardsoflua.lua.extension.util.LuaClassAttributes;

@AutoService(LuaConverter.class)
@LuaClassAttributes(name = PlayerLoggedInEventClass.NAME, superClass = EventClass.class)
@GenerateLuaClassTable(instance = PlayerLoggedInEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public class PlayerLoggedInEventClass
    extends BasicLuaClass<PlayerEvent.PlayerLoggedInEvent, PlayerLoggedInEventClass.Instance<?>> {
  public static final String NAME = "PlayerLoggedInEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new PlayerLoggedInEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<?>> toLuaInstance(PlayerEvent.PlayerLoggedInEvent javaInstance) {
    return new PlayerLoggedInEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends PlayerEvent.PlayerLoggedInEvent>
      extends EventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    @LuaProperty
    public EntityPlayer getPlayer() {
      return delegate.player;
    }
  }
}
