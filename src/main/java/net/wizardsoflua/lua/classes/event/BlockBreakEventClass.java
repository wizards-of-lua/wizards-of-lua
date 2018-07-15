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

@AutoService(LuaConverter.class)
@LuaClassAttributes(name = BlockBreakEventClass.NAME, superClass = BlockEventClass.class)
@GenerateLuaClassTable(instance = BlockBreakEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public class BlockBreakEventClass extends
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

    @LuaProperty
    public int getExperience() {
      return delegate.getExpToDrop();
    }

    @LuaProperty
    public EntityPlayer getPlayer() {
      return delegate.getPlayer();
    }
  }
}
