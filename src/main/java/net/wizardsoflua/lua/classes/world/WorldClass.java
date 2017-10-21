//package net.wizardsoflua.lua.classes.world;
//
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Vec3d;
//import net.minecraft.world.World;
//import net.sandius.rembulan.Table;
//import net.wizardsoflua.lua.Converters;
//import net.wizardsoflua.lua.classes.DeclareLuaClass;
//import net.wizardsoflua.lua.classes.LuaClass;
//import net.wizardsoflua.lua.classes.common.DelegatingProxy;
//
//@DeclareLuaClass(name = WorldClass.METATABLE_NAME)
//public class WorldClass extends LuaClass<World> {
//  public static final String METATABLE_NAME = "World";
//
//  public WorldClass() {
//    super(World.class);
//  }
//
//  @Override
//  public Table toLua(World javaObj) {
//    return new Proxy(getConverters(), getMetatable(), javaObj);
//  }
//
//  @Override
//  public World toJava(Table luaObj) {
//    Proxy proxy = getProxy(luaObj);
//    return proxy.delegate;
//  }
//
//  protected Proxy getProxy(Object luaObj) {
//    getConverters().getTypes().checkAssignable(METATABLE_NAME, luaObj);
//    return (Proxy) luaObj;
//  }
//
//  public static class Proxy extends DelegatingProxy {
//
//    private final World delegate;
//
//    public Proxy(Converters converters, Table metatable, World delegate) {
//      super(converters, metatable, delegate);
//      this.delegate = delegate;
//      addReadOnly("name", this::getWorldName);
//      add("worldspawn", this::getWorldSpawn, this::setWorldSpawn);
//    }
//
//    @Override
//    public boolean isTransferable() {
//      return false;
//    }
//
//    private Object getWorldName() {
//      String name = delegate.getWorldInfo().getWorldName();
//      return getConverters().toLua(name);
//    }
//    
//    private Object getWorldSpawn() {
//      BlockPos blockPos = delegate.getSpawnPoint();
//      return getConverters().toLua(new Vec3d(blockPos));
//    }
//
//    private void setWorldSpawn(Object luaObj) {
//      Vec3d pos = getConverters().toJava(Vec3d.class, luaObj);
//      delegate.setSpawnPoint(new BlockPos(pos));
//    }
//  }
//
//}
