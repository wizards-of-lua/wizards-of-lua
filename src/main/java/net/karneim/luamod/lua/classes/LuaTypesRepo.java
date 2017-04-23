package net.karneim.luamod.lua.classes;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;

public class LuaTypesRepo {

  private final Map<String, LuaClass> types = new HashMap<String, LuaClass>();
  private final Table env;

  public LuaTypesRepo(Table env) {
    this.env = checkNotNull(env);
  }

  public <T extends LuaClass> T get(Class<T> cls) {
    return get(LuaClass.getModuleNameOf(cls));
  }

  public <T extends LuaClass> T get(String name) {
    LuaClass obj = types.get(name);
    return (T) obj;
  }

  public Table getEnv() {
    return env;
  }

  public boolean isRegistered(String name) {
    return types.containsKey(name);
  }

  public <T extends LuaClass> void register(T luaClass) {
    String name = luaClass.getModuleName();
    if (types.containsKey(name)) {
      throw new IllegalArgumentException(String.format("Type %s is already definded!", luaClass));
    }
    types.put(name, luaClass);
  }

  public boolean wrap(boolean javaObject) {
    return javaObject;
  }

  public long wrap(byte javaObject) {
    return javaObject;
  }

  public double wrap(double javaObject) {
    return javaObject;
  }

  public DelegatingTable<Entity> wrap(Entity javaObject) {
    return get(EntityClass.class).toLuaObject(javaObject);
  }

  public ByteString wrap(Enum<?> javaObject) {
    return ByteString.of(javaObject.name());
  }

  public double wrap(float javaObject) {
    return javaObject;
  }

  public DelegatingTable<IBlockState> wrap(IBlockState javaObject) {
    return get(BlockStateClass.class).toLuaObject(javaObject);
  }

  public long wrap(int javaObject) {
    return javaObject;
  }

  public DelegatingTable<ItemStack> wrap(ItemStack javaObject) {
    return get(ItemStackClass.class).toLuaObject(javaObject);
  }

  public DelegatingTable<Iterable<ItemStack>> wrap(Iterable<ItemStack> javaObject) {
    return get(ArmorClass.class).toLuaObject(javaObject);
  }

  public long wrap(long javaObject) {
    return javaObject;
  }

  public DelegatingTable<Material> wrap(Material javaObject) {
    return get(MaterialClass.class).toLuaObject(javaObject);
  }

  public long wrap(short javaObject) {
    return javaObject;
  }

  public ByteString wrap(String javaObject) {
    return ByteString.of(javaObject);
  }

  public PatchedImmutableTable wrap(Vec3d javaObject) {
    return get(Vec3Class.class).toLuaObject(javaObject);
  }

  public PatchedImmutableTable wrapStrings(Iterable<String> javaObject) {
    PatchedImmutableTable.Builder builder = new PatchedImmutableTable.Builder();
    long idx = 0;
    for (String value : javaObject) {
      idx++;
      builder.add(idx, value);
    }
    return builder.build();
  }
}
