package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
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
 * The <span class="notranslate">AttackEntityEvent</span> is fired when a [player](/modules/Player)
 * attacks an [Entity](/modules/Entity).
 *
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = AttackEntityEventClass.NAME, superClass = EventClass.class)
@GenerateLuaClassTable(instance = AttackEntityEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public final class AttackEntityEventClass
    extends BasicLuaClass<AttackEntityEvent, AttackEntityEventClass.Instance<AttackEntityEvent>> {
  public static final String NAME = "AttackEntityEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new AttackEntityEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<AttackEntityEvent>> toLuaInstance(AttackEntityEvent javaInstance) {
    return new AttackEntityEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends AttackEntityEvent> extends EventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    /**
     * This is the [player](/modules/Player) that attacks the entity.
     *
     * #### Example
     *
     * Canceling the attack event when the player is invisible.
     *
     * <code>
     * Events.on('AttackEntityEvent'):call(function(event)
     *   if event.player.invisible then
     *     event.canceled = true
     *   end
     * end)
     * </code>
     */
    @LuaProperty
    public EntityPlayer getPlayer() {
      return delegate.getEntityPlayer();
    }

    /**
     * This is the [entity](/modules/Entity) that is attacked.
     *
     * #### Example
     *
     * Canceling the attack event when the target is a zombie.
     *
     * <code>
     * Events.on('AttackEntityEvent'):call(function(event)
     *   if event.target.entityType == "zombie" then
     *     event.canceled = true
     *   end
     * end)
     * </code>
     *
     */
    @LuaProperty
    public Entity getTarget() {
      return delegate.getTarget();
    }
  }
}
