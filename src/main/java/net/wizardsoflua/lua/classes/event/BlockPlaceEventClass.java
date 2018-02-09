package net.wizardsoflua.lua.classes.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.world.BlockEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.block.ImmutableWolBlock;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = BlockPlaceEventClass.METATABLE_NAME,
    superclassname = BlockEventClass.METATABLE_NAME)
public class BlockPlaceEventClass extends
    ProxyingLuaClass<BlockEvent.PlaceEvent, BlockPlaceEventClass.Proxy<BlockEvent.PlaceEvent>> {
  public static final String METATABLE_NAME = "BlockPlaceEvent";

  @Override
  public Proxy<BlockEvent.PlaceEvent> toLua(BlockEvent.PlaceEvent javaObj) {
    return new Proxy<>(getConverters(), getMetaTable(), javaObj);
  }

  public static class Proxy<D extends BlockEvent.PlaceEvent> extends BlockEventClass.Proxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addReadOnly("hand", this::getHand);
      addReadOnly("placedAgainst", this::getPlacedAgainst);
      addReadOnly("player", this::getPlayer);
    }

    @Override
    protected Object getBlock() {
      IBlockState blockState = delegate.getState();
      NBTTagCompound nbt = delegate.getBlockSnapshot().getNbt();
      return getConverters().toLua(new ImmutableWolBlock(blockState, nbt));
    }

    protected Object getHand() {
      return getConverters().toLua(delegate.getHand());
    }

    protected Object getPlacedAgainst() {
      IBlockState blockState = delegate.getPlacedAgainst();
      NBTTagCompound nbt = null;
      return getConverters().toLua(new ImmutableWolBlock(blockState, nbt));
    }

    protected Object getPlayer() {
      return getConverters().toLua(delegate.getPlayer());
    }
  }
}
