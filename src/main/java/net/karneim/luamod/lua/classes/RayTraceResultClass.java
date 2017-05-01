package net.karneim.luamod.lua.classes;

import static net.karneim.luamod.lua.util.LuaPreconditions.checkTypeDelegatingTable;
import static net.karneim.luamod.lua.util.LuaPreconditions.checkTypeInt;
import static net.karneim.luamod.lua.util.LuaPreconditions.checkTypeString;

import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Table;

@LuaModule("RayTraceResult")
public class RayTraceResultClass extends DelegatingLuaClass<RayTraceResult> {
  public RayTraceResultClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends RayTraceResult> b, RayTraceResult d) {
    b.addReadOnly("blockPos", () -> repo.wrap(d.getBlockPos()));
    b.add("entityHit", () -> repo.wrap(d.entityHit),
        o -> d.entityHit = checkTypeDelegatingTable(o, Entity.class));
    b.add("hitVec", () -> repo.wrap(d.hitVec),
        o -> d.hitVec = checkTypeDelegatingTable(o, Vec3d.class));
    b.add("sideHit", () -> repo.wrap(d.sideHit),
        o -> d.sideHit = EnumFacing.valueOf(checkTypeString(o)));
    b.add("subHit", () -> repo.wrap(d.subHit), o -> d.subHit = checkTypeInt(o));
    b.add("typeOfHit", () -> repo.wrap(d.typeOfHit),
        o -> d.typeOfHit = Type.valueOf(checkTypeString(o)));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
