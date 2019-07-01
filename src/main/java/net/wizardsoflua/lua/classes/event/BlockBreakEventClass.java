package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.world.BlockEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.common.Delegator;

/**
 * The <span class="notranslate">BlockBreakEvent</span> is fired when an Block is about to be broken
 * by a player.
 *
 * Canceling this event will prevent the Block from being broken.
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = BlockBreakEventClass.NAME, superClass = BlockEventClass.class)
@GenerateLuaClassTable(instance = BlockBreakEventClass.Instance.class)
@GenerateLuaDoc(subtitle = "When a Player Breaks a Block", type = EventClass.TYPE)
public final class BlockBreakEventClass extends
    BasicLuaClass<BlockEvent.BreakEvent, BlockBreakEventClass.Instance<BlockEvent.BreakEvent>> {
  public static final String NAME = "BlockBreakEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new BlockBreakEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<BlockEvent.BreakEvent>> toLuaInstance(
      BlockEvent.BreakEvent javaInstance) {
    return new BlockBreakEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends BlockEvent.BreakEvent>
      extends BlockEventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    /**
     * This is the amount of experience to drop by the block, if the event won't be canceled.
     *
     * #### Example
     *
     * Drop a number of diamonds equal to the amount of experience a player gains through mining
     * coal ore, redstone ore, etc. Note that you don't get experience from breaking blocks in
     * creative mode.
     *
     * <code>
     * local queue = Events.collect("BlockBreakEvent")
     * while true do
     *   local event = queue:next()
     *   spell.pos = event.pos
     *   if event.experience > 0 then
     *     spell:dropItem(Items.get('diamond', event.experience))
     *   end
     * end
     * </code>
     *
     * #### Example
     *
     * Drop 20 experience points when a players breaks a grass block.
     *
     * <code>
     * Events.on("BlockBreakEvent"):call(function(event)
     *   if event.block.name == "grass" then
     *     event.experience = 20
     *   end
     * end)
     * </code>
     */
    @LuaProperty
    public int getExperience() {
      return delegate.getExpToDrop();
    }

    @LuaProperty
    public void setExperience(int newValue) {
      delegate.setExpToDrop(newValue);
    }

    /**
     * This is the [player](../Player) who broke the block.
     */
    @LuaProperty
    public EntityPlayer getPlayer() {
      return delegate.getPlayer();
    }
  }
}
