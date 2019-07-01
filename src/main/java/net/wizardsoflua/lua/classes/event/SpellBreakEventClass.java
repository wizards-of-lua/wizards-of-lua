package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

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
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.spell.SpellBreakEvent;

/**
 * The <span class="notranslate">SpellBreakEvent</span> class informs about the termination of a
 * [Spell](../Spell).
 * 
 * A spell can intercept its own break event and do some clean-up before it is finally terminated.
 *
 * <code>
 * Events.on('SpellBreakEvent'):call(function(event)
 *   if event.spell == spell then
 *     -- do some clean-up here
 *   end
 * end)
 * </code>
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = SpellBreakEventClass.NAME, superClass = EventClass.class)
@GenerateLuaClassTable(instance = SpellBreakEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public final class SpellBreakEventClass
    extends BasicLuaClass<SpellBreakEvent, SpellBreakEventClass.Instance<SpellBreakEvent>> {
  public static final String NAME = "SpellBreakEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new SpellBreakEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<SpellBreakEvent>> toLuaInstance(SpellBreakEvent javaInstance) {
    return new SpellBreakEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends SpellBreakEvent> extends EventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    /**
     * The [Spell](../Spell) that is being terminated.
     */
    @LuaProperty
    public SpellEntity getSpell() {
      return delegate.getSpell();
    }

  }
}
