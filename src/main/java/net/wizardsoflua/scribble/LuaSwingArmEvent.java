package net.wizardsoflua.scribble;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.event.SwingArmEvent;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@GenerateLuaClass(name = "SwingArmEvent", superClass = LuaEventClass.class)
public class LuaSwingArmEvent<D extends SwingArmEvent> extends LuaEvent<D> {
  public LuaSwingArmEvent(ProxyingLuaClass<?, ?> luaClass, D delegate) {
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
