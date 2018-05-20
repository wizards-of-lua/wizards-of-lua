package net.wizardsoflua.lua.classes.vec3;

import javax.inject.Inject;

import com.google.auto.service.AutoService;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.spell.spi.JavaToLuaConverter;
import net.wizardsoflua.lua.converter.TypeTokenJavaToLuaConverter;

@AutoService(JavaToLuaConverter.class)
public class Vec3iConverter extends TypeTokenJavaToLuaConverter<Vec3i> {
  @Inject
  private Vec3Class vec3Class;

  @Override
  public String getName() {
    return vec3Class.getName();
  }

  @Override
  public Table getLuaInstance(Vec3i javaInstance) {
    Vec3d vec3d = new Vec3d(javaInstance);
    return vec3Class.getLuaInstance(vec3d);
  }
}
