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
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.common.Delegator;

/**
 * The <span class="notranslate">PlayerLoggedInEvent</span> is fired whenever a
 * [Player](/modules/Player) joins the world (server).
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = PlayerLoggedInEventClass.NAME, superClass = EventClass.class)
@GenerateLuaClassTable(instance = PlayerLoggedInEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE, subtitle = "When a Player Joins the World")
public final class PlayerLoggedInEventClass extends
    BasicLuaClass<PlayerEvent.PlayerLoggedInEvent, PlayerLoggedInEventClass.Instance<PlayerEvent.PlayerLoggedInEvent>> {
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
  protected Delegator<Instance<PlayerEvent.PlayerLoggedInEvent>> toLuaInstance(
      PlayerEvent.PlayerLoggedInEvent javaInstance) {
    return new PlayerLoggedInEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends PlayerEvent.PlayerLoggedInEvent>
      extends EventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    /**
     * The player who joined the world.
     */
    @LuaProperty
    public EntityPlayer getPlayer() {
      return delegate.getPlayer();
    }
  }
}
