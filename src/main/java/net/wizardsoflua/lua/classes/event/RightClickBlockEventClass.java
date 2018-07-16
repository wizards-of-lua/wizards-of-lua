package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
@LuaClassAttributes(name = RightClickBlockEventClass.NAME,
    superClass = PlayerInteractEventClass.class)
@GenerateLuaClassTable(instance = RightClickBlockEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public class RightClickBlockEventClass extends
    BasicLuaClass<PlayerInteractEvent.RightClickBlock, RightClickBlockEventClass.Instance<PlayerInteractEvent.RightClickBlock>> {
  public static final String NAME = "RightClickBlockEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new RightClickBlockEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<PlayerInteractEvent.RightClickBlock>> toLuaInstance(
      PlayerInteractEvent.RightClickBlock javaInstance) {
    return new RightClickBlockEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends PlayerInteractEvent.RightClickBlock>
      extends PlayerInteractEventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    @LuaProperty
    public Vec3d getHitVec() {
      return delegate.getHitVec();
    }
  }
}
