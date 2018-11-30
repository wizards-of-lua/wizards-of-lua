package net.wizardsoflua.lua.classes.entity;

import com.google.auto.service.AutoService;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Table;
import net.wizardsoflua.ai.WolMobAI;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.common.Delegator;

/**
 * The <span class="notranslate">Mob</span> class represents mobile creatures that are
 * self-controlled and have a distinct behaviour.
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = MobClass.NAME, superClass = EntityClass.class)
@GenerateLuaClassTable(instance = MobClass.Instance.class)
@GenerateLuaDoc(subtitle = "Mobile Creatures")
public final class MobClass extends BasicLuaClass<EntityLiving, MobClass.Instance<EntityLiving>> {
  public static final String NAME = "Mob";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new MobClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<EntityLiving>> toLuaInstance(EntityLiving javaInstance) {
    return new MobClassInstanceTable<>(new Instance<>(javaInstance, injector), getTable(),
        converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends EntityLiving> extends EntityLivingBaseClass.Instance<D> {
    public Instance(D delegate, Injector injector) {
      super(delegate, injector);
    }

    /**
     * The 'ai' property defines if this mobile creature is currently controlled by its artificial
     * intelligence (AI). If set to false this creature becomes dumb and just stands around. It even
     * won't react to physical forces. Default is true.
     */
    @LuaProperty
    public boolean getAi() {
      return !delegate.isAIDisabled();
    }

    @LuaProperty
    public void setAi(boolean ai) {
      delegate.setNoAI(!ai);
    }

    /**
     * /lua pig=Entities.summon('pig'); pig:walkTo(spell.pos+Vec3(10,0,0)) 
     */
    @LuaFunction
    public void walkTo(Vec3d target, float speed) {
      WolMobAI ai = getWolMobAI();
      ai.setDestination(target);
      ai.setSpeed(speed);
    }

    private WolMobAI getWolMobAI() {
      for (EntityAITaskEntry e : delegate.tasks.taskEntries) {
        if (e.action instanceof WolMobAI) {
          return (WolMobAI) e.action;
        }
      }
      WolMobAI result = new WolMobAI(delegate);
      delegate.tasks.addTask(1, result);
      return result;
    }
  }
}
