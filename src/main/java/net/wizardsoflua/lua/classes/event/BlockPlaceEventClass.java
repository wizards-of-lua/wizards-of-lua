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

/**
 * The <span class="notranslate">BlockPlaceEvent</span> class is fired when a player places a
 * [block](../Block).
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = BlockPlaceEventClass.NAME, superClass = BlockEventClass.class)
@GenerateLuaClassTable(instance = BlockPlaceEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE, subtitle = "When a Player Places a Block")
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

    /**
     * The block against which the new block was placed. Unfortunately the NBT of the block
     * placedAgainst is unavailable in this event.
     *
     * #### Example
     *
     * Transform all torches that are placed against a redstone block into redstone torches.
     *
     * <code>
     * local queue = Events.collect("BlockPlaceEvent")
     * while true do
     *   local event = queue:next()
     *   if event.block.name == 'torch' and event.placedAgainst.name == 'redstone_block' then
     *     spell.pos = event.pos
     *     spell.block = Blocks.get('redstone_torch'):withData(event.block.data)
     *   end
     * end
     * </code>
     *
     */
    @LuaProperty
    public ImmutableWolBlock getPlacedAgainst() {
      IBlockState blockState = delegate.getPlacedAgainst();
      NBTTagCompound nbt = null;
      return new ImmutableWolBlock(blockState, nbt);
    }

    /**
     * The block that is replaced by this event.
     */
    @LuaProperty
    public ImmutableWolBlock getReplacedBlock() {
      BlockSnapshot blockSnapshot = delegate.getBlockSnapshot();
      IBlockState blockState = blockSnapshot.getReplacedBlock();
      NBTTagCompound nbt = blockSnapshot.getNbt();
      return new ImmutableWolBlock(blockState, nbt);
    }

    /**
     * The player that triggered this event.
     */
    @LuaProperty
    public @Nullable Entity getEntity() {
      return delegate.getEntity();
    }
  }
}
