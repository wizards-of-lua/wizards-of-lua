package net.wizardsoflua.lua.module.types;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.auto.service.AutoService;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.TableFactory;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.api.resource.LuaTypes;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.BadArgumentException;
import net.wizardsoflua.lua.classes.ObjectClass2;
import net.wizardsoflua.lua.extension.util.LuaTableExtension;

@GenerateLuaModuleTable
@GenerateLuaDoc(name = TypesModule.NAME)
@AutoService(SpellExtension.class)
public class TypesModule extends LuaTableExtension {
  public static final String NAME = "Types";
  @Resource
  private LuaConverters converters;
  @Resource
  private Table env;
  @Resource
  private TableFactory tableFactory;
  @Resource
  private LuaTypes types;
  @Inject
  private ObjectClass2 objectClass;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table getTable() {
    return new TypesModuleTable<>(this, converters);
  }

  @LuaFunction
  public void declare(String className, @Nullable Table metatable) {
    if (env.rawget(className) != null) {
      throw new BadArgumentException(
          "a global variable with name '" + className + "' is already defined", 1, "className",
          "declare");
    }
    if (metatable == null) {
      metatable = objectClass.getTable();
    }
    Table classTable = tableFactory.newTable();
    classTable.rawset("__index", classTable);
    classTable.setMetatable(metatable);
    types.registerLuaClass(className, classTable);
    env.rawset(className, classTable);
  }

  @LuaFunction
  public boolean instanceOf(Table classTable, @Nullable Object object) {
    if (object == null) {
      return false;
    }
    if (!(object instanceof Table)) {
      return false;
    }
    Table metatable = ((Table) object).getMetatable();
    return classTable.equals(metatable) || instanceOf(classTable, metatable);
  }

  @LuaFunction
  public String type(@Nullable Object LuaObject) {
    return types.getLuaTypeNameOfLuaObject(LuaObject);
  }
}
