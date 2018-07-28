package net.wizardsoflua.lua.classes.event;

import javax.annotation.Nullable;
import javax.inject.Inject;
import com.google.auto.service.AutoService;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.event.CustomLuaEvent;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.view.ViewFactory;

@AutoService(LuaConverter.class)
@LuaClassAttributes(name = CustomEventClass.NAME, superClass = EventClass.class)
@GenerateLuaClassTable(instance = CustomEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public final class CustomEventClass extends BasicLuaClass<CustomLuaEvent, CustomEventClass.Instance<CustomLuaEvent>> {
  public static final String NAME = "CustomEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new CustomEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<CustomLuaEvent>> toLuaInstance(CustomLuaEvent javaInstance) {
    return new CustomEventClassInstanceTable<>(new Instance<>(javaInstance, getName(), injector),
        getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends CustomLuaEvent> extends EventClass.Instance<D> {
    @Inject
    private ViewFactory viewFactory;

    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    @Override
    public String getName() {
      return delegate.getName();
    }

    @LuaProperty
    public @Nullable Object getData() {
      return delegate.getData(viewFactory);
    }
  }
}
