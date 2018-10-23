package net.wizardsoflua.lua.classes.nbt;

import com.google.auto.service.AutoService;

import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.classes.AnnotatedLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;

@AutoService(SpellExtension.class)
@LuaClassAttributes(name = "Nbt")
@GenerateLuaClassTable
@GenerateLuaDoc
public class NbtClass extends AnnotatedLuaClass {
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new NbtClassTable<>(this, converters);
  }

  @LuaFunction
  public boolean isAttached(NbtTable<?> self) {
    return self.isAttached();
  }
}
