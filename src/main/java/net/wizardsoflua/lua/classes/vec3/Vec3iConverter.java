package net.wizardsoflua.lua.classes.vec3;

import javax.inject.Inject;

import com.google.auto.service.AutoService;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.spell.spi.JavaToLuaConverter;
import net.wizardsoflua.lua.converter.AnnotatedJavaToLuaConverter;
import net.wizardsoflua.lua.converter.LuaConverterAttributes;

@AutoService(JavaToLuaConverter.class)
@LuaConverterAttributes(name = Vec3Class.NAME)
public class Vec3iConverter extends AnnotatedJavaToLuaConverter<Vec3i> {
  @Inject
  private Vec3Class vec3Class;

  @Override
  public Table getLuaInstance(Vec3i javaInstance) {
    Vec3d vec3d = new Vec3d(javaInstance);
    return vec3Class.getLuaInstance(vec3d);
  }
}
