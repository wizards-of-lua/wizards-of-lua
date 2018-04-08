package net.wizardsoflua.lua.classes.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.world.BlockEvent;
import net.wizardsoflua.block.ImmutableWolBlock;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = BlockEventClass.NAME, superClass = EventClass.class)
public class BlockEventClass
    extends ProxyingLuaClass<BlockEvent, BlockEventClass.Proxy<BlockEvent>> {
  public static final String NAME = "BlockEvent";

  @Override
  public Proxy<BlockEvent> toLua(BlockEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends BlockEvent> extends EventClass.Proxy<EventApi<D>, D> {
    public Proxy(ProxyingLuaClass<?, ?> luaClass, D delegate) {
      super(new EventApi<>(luaClass, delegate));
      addReadOnly("pos", this::getPos);
      addReadOnly("block", this::getBlock);
    }

    protected Object getPos() {
      return getConverter().toLua(new Vec3d(delegate.getPos()));
    }

    protected Object getBlock() {
      IBlockState blockState = delegate.getState();
      NBTTagCompound nbt = null;
      return getConverter().toLua(new ImmutableWolBlock(blockState, nbt));
    }
  }
}
