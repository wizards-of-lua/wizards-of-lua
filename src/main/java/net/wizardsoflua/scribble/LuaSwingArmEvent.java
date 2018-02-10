package net.wizardsoflua.scribble;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.wizardsoflua.annotation.LuaModule;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.event.SwingArmEvent;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@LuaModule(name = "SwingArmEvent", superClass = LuaEventClass.class)
public class LuaSwingArmEvent extends LuaApi<SwingArmEvent> {
  public LuaSwingArmEvent(ProxyingLuaClass<?, ?> luaClass, SwingArmEvent delegate) {
    super(luaClass, delegate);
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
