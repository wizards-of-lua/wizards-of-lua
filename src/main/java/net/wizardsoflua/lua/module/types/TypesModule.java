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
import net.wizardsoflua.lua.classes.ObjectClass;
import net.wizardsoflua.lua.extension.LuaTableExtension;

/**
 * The <span class="notranslate">Types</span> module can be used to check objects for their type and
 * to create new types.
 */
@AutoService(SpellExtension.class)
@GenerateLuaModuleTable
@GenerateLuaDoc(name = TypesModule.NAME, subtitle = "Managing Types")
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
  private ObjectClass objectClass;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table getTable() {
    return new TypesModuleTable<>(this, converters);
  }

  /**
   * The 'declare' function creates a new class with the given name and the optionally given
   * superclass.
   *
   * #### Example
   *
   * Declaring a "Book" class with some functions.
   *
   * <code>
   * Types.declare("Book")
   *
   * function Book.new(title)
   *   local o = {title=title, pages={}}
   *   setmetatable(o,Book)
   *   return o
   * end
   *
   * function Book:addPage(text)
   *   table.insert(self.pages, text)
   * end
   * </code>
   *
   * Please note that there is also a shortcut for "Types.declare":
   *
   * <code>
   * declare("Book")
   </code>
   *
   * #### Example
   *
   * Declaring the "Newspaper" class as a subclass of "Book".
   *
   * <code>
   * declare("Newspaper", Book)
   *
   * function Newspaper.new(title)
   *   local o = {title=title, pages={}}
   *   setmetatable(o,Newspaper)
   *   return o
   * end
   * </code>
   */
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

  /**
   * The 'instanceOf' function returns true if the given object is an instance of the given class.
   *
   * #### Example
   *
   * Checking if the current spell's owner is a player.
   *
   * <code>
   * if Types.instanceOf(Player, spell.owner) then
   *   print("Owner is a player")
   * end
   * </code>
   */
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

  /**
   *
   * The 'type' function returns the name of the given object's type.
   *
   * #### Example
   *
   * Printing the type of the spell's owner.
   *
   * <code>
   * print( Types.type(spell.owner))
   * </code>
   *
   * Since "Types.type" is widely used there exists also a shortcut: "type".
   *
   * <code>
   * print( type(spell.owner))
   * </code>
   */
  @LuaFunction
  public String type(@Nullable Object LuaObject) {
    return types.getLuaTypeNameOfLuaObject(LuaObject);
  }
}
