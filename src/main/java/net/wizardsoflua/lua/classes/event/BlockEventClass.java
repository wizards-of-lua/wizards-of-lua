package net.wizardsoflua.lua.classes.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.world.BlockEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.block.ImmutableWolBlock;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = BlockEventClass.METATABLE_NAME, superclassname = EventClass.METATABLE_NAME)
public class BlockEventClass
    extends ProxyingLuaClass<BlockEvent, BlockEventClass.Proxy<BlockEvent>> {
  public static final String METATABLE_NAME = "BlockEvent";

  @Override
  protected String getMetatableName() {
    return METATABLE_NAME;
  }

  @Override
  public Proxy<BlockEvent> toLua(BlockEvent javaObj) {
    return new Proxy<>(getConverters(), getMetatable(), javaObj);
  }

  public static class Proxy<D extends BlockEvent> extends EventClass.Proxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addReadOnly("pos", this::getPos);
      addReadOnly("block", this::getBlock);
    }

    protected Object getPos() {
      return getConverters().toLua(new Vec3d(delegate.getPos()));
    }

    protected Object getBlock() {
      IBlockState blockState = delegate.getState();
      TileEntity tileEntity = delegate.getWorld().getTileEntity(delegate.getPos());
      return getConverters().toLua(new ImmutableWolBlock(blockState, tileEntity));
    }
  }
}
