package net.wizardsoflua.scribble;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.wizardsoflua.event.SwingArmEvent;

public class LuaSwingArmEventProxy<A extends LuaSwingArmEvent<D>, D extends SwingArmEvent>
    extends LuaEventProxy<A, D> {
  public LuaSwingArmEventProxy(A api) {
    super(api);
    addReadOnly("hand", this::getHand);
    addReadOnly("item", this::getItem);
    addReadOnly("player", this::getPlayer);
  }

  private Object getHand() {
    EnumHand result = api.getHand();
    return getConverters().toLuaNullable(result);
  }

  private Object getItem() {
    ItemStack result = api.getItem();
    return getConverters().toLuaNullable(result);
  }

  private Object getPlayer() {
    EntityPlayer result = api.getPlayer();
    return getConverters().toLuaNullable(result);
  }
}
