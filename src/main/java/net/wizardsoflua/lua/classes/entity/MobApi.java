package net.wizardsoflua.lua.classes.entity;

import net.minecraft.entity.EntityLiving;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

/**
 * The <span class="notranslate">Mob</span> class represents mobile creatures that are
 * self-controlled and have a distinct behaviour.
 */
@GenerateLuaClass(name = MobApi.NAME)
@GenerateLuaDoc(subtitle = "Mobile Creatures")
public class MobApi<D extends EntityLiving> extends EntityLivingBaseApi<D> {
  public static final String NAME = "Mob";

  public MobApi(ProxyingLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
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
}
