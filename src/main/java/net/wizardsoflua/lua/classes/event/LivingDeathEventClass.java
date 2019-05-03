package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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
 * The <span class="notranslate">LivingDeathEvent</span> class is fired when an entity dies.
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = LivingDeathEventClass.NAME, superClass = LivingEventClass.class)
@GenerateLuaClassTable(instance = LivingDeathEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE, subtitle = "When a Living Entity Dies")
public final class LivingDeathEventClass
    extends BasicLuaClass<LivingDeathEvent, LivingDeathEventClass.Instance<LivingDeathEvent>> {
  public static final String NAME = "LivingDeathEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new LivingDeathEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<LivingDeathEvent>> toLuaInstance(LivingDeathEvent javaInstance) {
    return new LivingDeathEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends LivingDeathEvent> extends LivingEventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    /**
     * The cause of death. This is something like 'drown', 'lava', 'fall', etc.
     *
     * #### Example
     *
     * Rewarding a player who died in lava with a brand new lava bucket.
     *
     * <code>
     * local causes = {}
     * local queue = Events.collect("LivingDeathEvent","PlayerRespawnEvent")
     * while true do
     *   local event = queue:next()
     *   if event.name == "LivingDeathEvent" and type(event.entity)=="Player" then
     *     causes[event.entity.name] = event.cause
     *   elseif event.name == "PlayerRespawnEvent" then
     *     if causes[event.player.name]=="lava" then
     *       spell:execute("/give %s minecraft:lava_bucket", event.player.name)
     *     end
     *   end
     * end
     * </code>
     */
    @LuaProperty
    public String getCause() {
      DamageSource source = delegate.getSource();
      return source.damageType;
    }
  }
}
