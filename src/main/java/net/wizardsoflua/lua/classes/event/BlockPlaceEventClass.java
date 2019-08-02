package net.wizardsoflua.lua.classes.event;

import javax.annotation.Nullable;
import com.google.auto.service.AutoService;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.BlockSnapshot;
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

@AutoService(LuaConverter.class)
@LuaClassAttributes(name = BlockPlaceEventClass.NAME, superClass = BlockEventClass.class)
@GenerateLuaClassTable(instance = BlockPlaceEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public final class BlockPlaceEventClass extends
    BasicLuaClass<BlockEvent.EntityPlaceEvent, BlockPlaceEventClass.Instance<BlockEvent.EntityPlaceEvent>> {
  public static final String NAME = "BlockPlaceEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new BlockPlaceEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<BlockEvent.EntityPlaceEvent>> toLuaInstance(
      BlockEvent.EntityPlaceEvent javaInstance) {
    return new BlockPlaceEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends BlockEvent.EntityPlaceEvent>
      extends BlockEventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    @LuaProperty
    public ImmutableWolBlock getPlacedAgainst() {
      IBlockState blockState = delegate.getPlacedAgainst();
      NBTTagCompound nbt = null;
      return new ImmutableWolBlock(blockState, nbt);
    }

    @LuaProperty
    public ImmutableWolBlock getReplacedBlock() {
      BlockSnapshot blockSnapshot = delegate.getBlockSnapshot();
      IBlockState blockState = blockSnapshot.getReplacedBlock();
      NBTTagCompound nbt = blockSnapshot.getNbt();
      return new ImmutableWolBlock(blockState, nbt);
    }

    @LuaProperty
    public @Nullable Entity getEntity() {
      return delegate.getEntity();
    }
  }
}
