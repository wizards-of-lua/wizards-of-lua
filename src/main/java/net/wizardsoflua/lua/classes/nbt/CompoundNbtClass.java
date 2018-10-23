package net.wizardsoflua.lua.classes.nbt;

import com.google.auto.service.AutoService;

import net.minecraft.nbt.NBTTagCompound;
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
@LuaClassAttributes(name = "CompoundNbt", superClass = NbtClass.class)
@GenerateLuaClassTable
@GenerateLuaDoc
public class CompoundNbtClass extends AnnotatedLuaClass {
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new CompoundNbtClassTable<>(this, converters);
  }

  public CompoundNbtTable toLuaInstance(NbtAccessor<NBTTagCompound> accessor) {
    return new CompoundNbtTable(accessor, getTable(), injector);
  }
}
