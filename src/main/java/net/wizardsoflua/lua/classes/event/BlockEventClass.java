package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.world.BlockEvent;
import net.wizardsoflua.block.ImmutableWolBlock;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;
import net.wizardsoflua.lua.classes.spi.DeclaredLuaClass;

@AutoService(DeclaredLuaClass.class)
@DeclareLuaClass(name = BlockEventClass.NAME, superClass = EventClass.class)
public class BlockEventClass
    extends DelegatorLuaClass<BlockEvent, BlockEventClass.Proxy<BlockEvent>> {
  public static final String NAME = "BlockEvent";

  @Override
  public Proxy<BlockEvent> toLua(BlockEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends BlockEvent> extends EventClass.Proxy<EventApi<D>, D> {
    public Proxy(DelegatorLuaClass<?, ?> luaClass, D delegate) {
      super(new EventApi<>(luaClass, delegate));
      addReadOnly("pos", this::getPos);
      addReadOnly("block", this::getBlock);
    }

    protected Object getPos() {
      return getConverters().toLua(new Vec3d(delegate.getPos()));
    }

    protected Object getBlock() {
      IBlockState blockState = delegate.getState();
      BlockPos pos = delegate.getPos();
      TileEntity tileEntity = delegate.getWorld().getTileEntity(pos);
      return getConverters().toLua(new ImmutableWolBlock(blockState, tileEntity));
    }
  }
}
