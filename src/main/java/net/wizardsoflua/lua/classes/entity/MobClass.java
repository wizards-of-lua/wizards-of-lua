package net.wizardsoflua.lua.classes.entity;

import javax.annotation.Nullable;

import com.google.auto.service.AutoService;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
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

    // TODO remove the following part when the lua doc generator is fixed

    /**
     * The 'health' is the energy of this entity. When it falls to zero this entity dies.
     */
    @Override
    @LuaProperty
    public float getHealth() {
      // NOTE: this method is redeclared here to force the lua doc generator to write this docs
      return super.getHealth();
    }

    @Override
    @LuaProperty
    public void setHealth(float value) {
      // NOTE: this method is redeclared here to force the lua doc generator to write this docs
      super.setHealth(value);
    }

    /**
     * This is the [item](/modules/Item) this entity is holding in its main hand.
     */
    @Override
    @LuaProperty
    public @Nullable ItemStack getMainhand() {
      // NOTE: this method is redeclared here to force the lua doc generator to write this docs
      return super.getMainhand();
    }

    @Override
    @LuaProperty
    public void setMainhand(@Nullable ItemStack mainhand) {
      // NOTE: this method is redeclared here to force the lua doc generator to write this docs
      super.setMainhand(mainhand);
    }

    /**
     * This is the [item](/modules/Item) this entity is holding in his off hand.
     */
    @Override
    @LuaProperty
    public @Nullable ItemStack getOffhand() {
      // NOTE: this method is redeclared here to force the lua doc generator to write this docs
      return super.getOffhand();
    }

    @Override
    @LuaProperty
    public void setOffhand(@Nullable ItemStack offhand) {
      // NOTE: this method is redeclared here to force the lua doc generator to write this docs
      super.setOffhand(offhand);
    }
  }
}
