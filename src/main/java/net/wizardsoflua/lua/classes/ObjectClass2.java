package net.wizardsoflua.lua.classes;

import javax.annotation.Nullable;

import com.google.auto.service.AutoService;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.TableFactory;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.extension.util.AnnotatedLuaClass;
import net.wizardsoflua.lua.extension.util.LuaClassAttributes;

@AutoService(SpellExtension.class)
@LuaClassAttributes(name = ObjectClass2.NAME)
public class ObjectClass2 extends AnnotatedLuaClass {
  public static final String NAME = "Object";
  @Resource
  private TableFactory tableFactory;

  @Override
  protected @Nullable Table getMetatable() {
    return null;
  }

  @Override
  protected Table createRawTable() {
    return tableFactory.newTable();
  }
}
