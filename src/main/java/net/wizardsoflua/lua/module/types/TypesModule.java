package net.wizardsoflua.lua.module.types;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nullable;

import com.google.auto.service.AutoService;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.TableFactory;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.lua.BadArgumentException;
import net.wizardsoflua.lua.classes.ObjectClass2;
import net.wizardsoflua.lua.extension.api.inject.AfterInjection;
import net.wizardsoflua.lua.extension.api.inject.Inject;
import net.wizardsoflua.lua.extension.api.service.LuaConverters;
import net.wizardsoflua.lua.extension.api.service.SpellExtensions;
import net.wizardsoflua.lua.extension.spi.SpellExtension;
import net.wizardsoflua.lua.extension.util.LuaTableExtension;

@GenerateLuaModuleTable
@GenerateLuaDoc(name = TypesModule.NAME)
@AutoService(SpellExtension.class)
public class TypesModule extends LuaTableExtension {
  public static final String NAME = "Types";
  public static final String BOOLEAN = "boolean";
  public static final String FUNCTION = "function";
  public static final String NIL = "nil";
  public static final String NUMBER = "number";
  public static final String STRING = "string";
  public static final String TABLE = "table";
  @Inject
  private LuaConverters converters;
  @Inject
  private Table env;
  @Inject
  private SpellExtensions extensions;
  @Inject
  private TableFactory tableFactory;

  private final BiMap<String, Table> classes = HashBiMap.create();
  private Table objectClassTable;

  @AfterInjection
  public void init() {
    ObjectClass2 objectClass = extensions.getSpellExtension(ObjectClass2.class);
    objectClassTable = objectClass.getTable();
    registerClass(objectClass.getName(), objectClassTable);
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table getTable() {
    return new TypesModuleTable<>(this, converters);
  }

  public @Nullable Table getClassTableForName(String className) {
    requireNonNull(className, "className == null!");
    return classes.get(className);
  }

  public Table registerClass(String className, Table classTable) {
    requireNonNull(className, "className == null!");
    requireNonNull(classTable, "classTable == null!");
    return classes.put(className, classTable);
  }

  @LuaFunction
  public void declare(String className, @Nullable Table metatable) {
    if (env.rawget(className) != null) {
      throw new BadArgumentException(
          "a global variable with name '" + className + "' is already defined", 1, "className",
          "declare");
    }
    if (metatable == null) {
      metatable = objectClassTable;
    }
    Table classTable = tableFactory.newTable();
    classTable.rawset("__index", classTable);
    classTable.setMetatable(metatable);
    registerClass(className, classTable);
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
  public String getTypename(@Nullable Object object) {
    if (object == null) {
      return NIL;
    }
    if (object instanceof Table) {
      Table table = (Table) object;
      String className = getClassName(table);
      if (className != null) {
        return className;
      }
    }
    return getTypename(object.getClass());
  }

  public @Nullable String getClassName(Table instanceTable) {
    requireNonNull(instanceTable, "table == null!");
    if (classes.inverse().containsKey(instanceTable)) {
      return "class";
    }
    BiMap<Table, String> inverse = classes.inverse();
    Table metatable = instanceTable.getMetatable();
    return inverse.get(metatable);
  }

  public String getTypename(Class<?> cls) {
    // TODO Adrodoc 14.04.2018: respect ConverterExtensions
    if (Table.class.isAssignableFrom(cls)) {
      return TABLE;
    }
    if (ByteString.class.isAssignableFrom(cls) || String.class.isAssignableFrom(cls)) {
      return STRING;
    }
    if (Number.class.isAssignableFrom(cls)) {
      return NUMBER;
    }
    if (Boolean.class.isAssignableFrom(cls)) {
      return BOOLEAN;
    }
    if (net.sandius.rembulan.runtime.LuaFunction.class.isAssignableFrom(cls)) {
      return FUNCTION;
    }
    return cls.getName();
  }
}
