package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.block.ImmutableWolBlock;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.common.Delegator;

/**
 * The <span class="notranslate">BlockEvent</span> is the base class of
 * [BlockBreakEvent](/modules/BlockBreakEvent/) and [BlockPlaceEvent](/modules/BlockPlaceEvent/).
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = BlockEventClass.NAME, superClass = EventClass.class)
@GenerateLuaClassTable(instance = BlockEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public final class BlockEventClass
    extends BasicLuaClass<BlockEvent, BlockEventClass.Instance<BlockEvent>> {
  public static final String NAME = "BlockEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new BlockEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<BlockEvent>> toLuaInstance(BlockEvent javaInstance) {
    return new BlockEventClassInstanceTable<>(new Instance<>(javaInstance, getName(), injector),
        getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends BlockEvent> extends EventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    /**
     * The block's position.
     */
    @LuaProperty
    public BlockPos getPos() {
      return delegate.getPos();
    }

    /**
     * This is the block this event is about.
     */
    @LuaProperty
    public ImmutableWolBlock getBlock() {
      IBlockState blockState = delegate.getState();
      BlockPos pos = delegate.getPos();
      TileEntity tileEntity = delegate.getWorld().getTileEntity(pos);
      return new ImmutableWolBlock(blockState, tileEntity);
    }
  }
}
