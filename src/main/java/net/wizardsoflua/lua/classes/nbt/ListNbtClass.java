package net.wizardsoflua.lua.classes.nbt;

import com.google.auto.service.AutoService;

import net.minecraft.nbt.NBTTagList;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.classes.AnnotatedLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.nbt.accessor.NbtAccessor;

@AutoService(SpellExtension.class)
@LuaClassAttributes(name = "ListNbt", superClass = NbtClass.class)
@GenerateLuaClassTable
@GenerateLuaDoc
public class ListNbtClass extends AnnotatedLuaClass {
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new ListNbtClassTable<>(this, converters);
  }

  public ListNbtTable toLuaInstance(NbtAccessor<NBTTagList> accessor) {
    return new ListNbtTable(accessor, getTable(), injector);
  }
}
