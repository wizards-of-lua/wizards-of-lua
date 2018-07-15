package net.wizardsoflua.lua.classes.vec3;

import com.google.auto.service.AutoService;

import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.TableFactory;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.AnnotatedLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.table.DefaultTableBuilder;

@AutoService(LuaConverter.class)
@LuaClassAttributes(name = Vec3Class.NAME)
@GenerateLuaDoc(subtitle = "Manipulating Location and Motion")
public class Vec3Class extends AnnotatedLuaClass implements LuaConverter<Vec3d, Table> {
  public static final String NAME = "Vec3";
  @Resource
  private TableFactory factory;

  @Override
  protected Table createRawTable() {
    return factory.newTable();
  }

  @Override
  public Class<Vec3d> getJavaClass() {
    return Vec3d.class;
  }

  @Override
  public Class<Table> getLuaClass() {
    return Table.class;
  }

  @Override
  public Vec3d getJavaInstance(Table luaInstance) {
    double x = Conversions.floatValueOf(luaInstance.rawget("x"));
    double y = Conversions.floatValueOf(luaInstance.rawget("y"));
    double z = Conversions.floatValueOf(luaInstance.rawget("z"));
    return new Vec3d(x, y, z);
  }

  @Override
  public Table getLuaInstance(Vec3d javaInstance) {
    DefaultTableBuilder builder = new DefaultTableBuilder();
    builder.add("x", javaInstance.x);
    builder.add("y", javaInstance.y);
    builder.add("z", javaInstance.z);
    builder.setMetatable(getTable());
    return builder.build();
  }
}
