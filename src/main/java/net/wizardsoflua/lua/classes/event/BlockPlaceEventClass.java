package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;
import net.wizardsoflua.block.ImmutableWolBlock;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;
import net.wizardsoflua.lua.classes.spi.DeclaredLuaClass;

@AutoService(DeclaredLuaClass.class)
@DeclareLuaClass(name = BlockPlaceEventClass.NAME, superClass = BlockEventClass.class)
public class BlockPlaceEventClass extends
    DelegatorLuaClass<BlockEvent.PlaceEvent, BlockPlaceEventClass.Proxy<BlockEvent.PlaceEvent>> {
  public static final String NAME = "BlockPlaceEvent";

  @Override
  public Proxy<BlockEvent.PlaceEvent> toLua(BlockEvent.PlaceEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends BlockEvent.PlaceEvent> extends BlockEventClass.Proxy<D> {
    public Proxy(DelegatorLuaClass<?, ?> luaClass, D delegate) {
      super(luaClass, delegate);
      addReadOnly("hand", this::getHand);
      addReadOnly("placedAgainst", this::getPlacedAgainst);
      addReadOnly("replacedBlock", this::getReplacedBlock);
      addReadOnly("player", this::getPlayer);
    }

    protected Object getHand() {
      return getConverters().toLua(delegate.getHand());
    }

    protected Object getPlacedAgainst() {
      IBlockState blockState = delegate.getPlacedAgainst();
      NBTTagCompound nbt = null;
      return getConverters().toLua(new ImmutableWolBlock(blockState, nbt));
    }

    protected Object getReplacedBlock() {
      BlockSnapshot blockSnapshot = delegate.getBlockSnapshot();
      IBlockState blockState = blockSnapshot.getReplacedBlock();
      NBTTagCompound nbt = blockSnapshot.getNbt();
      return getConverters().toLua(new ImmutableWolBlock(blockState, nbt));
    }

    protected Object getPlayer() {
      return getConverters().toLua(delegate.getPlayer());
    }
  }
}
