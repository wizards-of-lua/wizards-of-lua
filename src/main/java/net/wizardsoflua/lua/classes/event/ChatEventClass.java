package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ServerChatEvent;
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
@LuaClassAttributes(name = ChatEventClass.NAME, superClass = EventClass.class)
@GenerateLuaClassTable(instance = ChatEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public class ChatEventClass
    extends BasicLuaClass<ServerChatEvent, ChatEventClass.Instance<ServerChatEvent>> {
  public static final String NAME = "ChatEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new ChatEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<ServerChatEvent>> toLuaInstance(ServerChatEvent javaInstance) {
    return new ChatEventClassInstanceTable<>(new Instance<>(javaInstance, getName(), injector),
        getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends ServerChatEvent> extends EventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    @LuaProperty
    public String getMessage() {
      return delegate.getMessage();
    }

    @LuaProperty
    public EntityPlayerMP getPlayer() {
      return delegate.getPlayer();
    }
  }
}
