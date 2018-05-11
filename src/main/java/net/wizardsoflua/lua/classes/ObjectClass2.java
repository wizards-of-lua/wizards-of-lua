package net.wizardsoflua.lua.classes;

import javax.annotation.Nullable;

import com.google.auto.service.AutoService;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.TableFactory;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.extension.util.LuaClass;

@AutoService(SpellExtension.class)
@GenerateLuaDoc(name = ObjectClass2.NAME, type = "class")
public class ObjectClass2 extends LuaClass {
  public static final String NAME = "Object";
  @Resource
  private TableFactory tableFactory;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  protected Class<? extends LuaClass> getSuperClassClass() {
    return null;
  }

  @Override
  protected @Nullable Table getMetatable() {
    return null;
  }

  @Override
  protected Table createRawTable() {
    return tableFactory.newTable();
  }
}
