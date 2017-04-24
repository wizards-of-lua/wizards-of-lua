package net.karneim.luamod.lua.classes;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import net.karneim.luamod.cursor.Spell;
import net.karneim.luamod.lua.classes.event.AnimationHandEventClass;
import net.karneim.luamod.lua.classes.event.ClickWindowEventClass;
import net.karneim.luamod.lua.classes.event.CustomLuaEventClass;
import net.karneim.luamod.lua.classes.event.EntityEventClass;
import net.karneim.luamod.lua.classes.event.EventClass;
import net.karneim.luamod.lua.classes.event.LivingEventClass;
import net.karneim.luamod.lua.classes.event.PlayerEventClass;
import net.karneim.luamod.lua.classes.event.PlayerInteractEventClass;
import net.karneim.luamod.lua.classes.event.RightClickBlockEventClass;
import net.karneim.luamod.lua.classes.event.ServerChatEventClass;
import net.karneim.luamod.lua.classes.event.WhisperEventClass;
import net.karneim.luamod.lua.event.AnimationHandEvent;
import net.karneim.luamod.lua.event.ClickWindowEvent;
import net.karneim.luamod.lua.event.CustomLuaEvent;
import net.karneim.luamod.lua.event.WhisperEvent;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;

public class LuaTypesRepo {

  private final Map<String, LuaClass> types = new HashMap<>();
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

  public PatchedImmutableTable wrap(AnimationHandEvent javaObject) {
    return get(AnimationHandEventClass.class).toLuaObject(javaObject);
  }

  public boolean wrap(boolean javaObject) {
    return javaObject;
  }

  public long wrap(byte javaObject) {
    return javaObject;
  }

  public ByteString wrap(ByteString javaObject) {
    return javaObject;
  }

  public PatchedImmutableTable wrap(ClickWindowEvent javaObject) {
    return get(ClickWindowEventClass.class).toLuaObject(javaObject);
  }

  public PatchedImmutableTable wrap(CustomLuaEvent javaObject) {
    return get(CustomLuaEventClass.class).toLuaObject(javaObject);
  }

  public double wrap(double javaObject) {
    return javaObject;
  }

  public DelegatingTable<? extends Entity> wrap(Entity javaObject) {
    if (javaObject instanceof EntityLivingBase) {
      return wrap((EntityLivingBase) javaObject);
    }
    return get(EntityClass.class).toLuaObject(javaObject);
  }

  public PatchedImmutableTable wrap(EntityEvent javaObject) {
    if (javaObject instanceof LivingEvent) {
      wrap((LivingEvent) javaObject);
    }
    return get(EntityEventClass.class).toLuaObject(javaObject);
  }

  public DelegatingTable<? extends EntityLiving> wrap(EntityLiving javaObject) {
    return get(EntityLivingClass.class).toLuaObject(javaObject);
  }

  public DelegatingTable<? extends EntityLivingBase> wrap(EntityLivingBase javaObject) {
    if (javaObject instanceof EntityLiving) {
      return wrap((EntityLiving) javaObject);
    }
    if (javaObject instanceof EntityPlayer) {
      return wrap((EntityPlayer) javaObject);
    }
    return get(EntityLivingBaseClass.class).toLuaObject(javaObject);
  }

  public DelegatingTable<? extends EntityPlayer> wrap(EntityPlayer javaObject) {
    return get(EntityPlayerClass.class).toLuaObject(javaObject);
  }

  public ByteString wrap(Enum<?> javaObject) {
    return ByteString.of(javaObject.name());
  }

  public PatchedImmutableTable wrap(Event javaObject) {
    if (javaObject instanceof CustomLuaEvent) {
      wrap((CustomLuaEvent) javaObject);
    }
    if (javaObject instanceof EntityEvent) {
      wrap((EntityEvent) javaObject);
    }
    if (javaObject instanceof ServerChatEvent) {
      wrap((ServerChatEvent) javaObject);
    }
    if (javaObject instanceof WhisperEvent) {
      wrap((WhisperEvent) javaObject);
    }
    return get(EventClass.class).toLuaObject(javaObject);
  }

  public double wrap(float javaObject) {
    return javaObject;
  }

  public PatchedImmutableTable wrap(IBlockState javaObject) {
    return get(BlockStateClass.class).toLuaObject(javaObject);
  }

  public long wrap(int javaObject) {
    return javaObject;
  }

  public DelegatingTable<? extends ItemStack> wrap(ItemStack javaObject) {
    return get(ItemStackClass.class).toLuaObject(javaObject);
  }

  public DelegatingTable<? extends Iterable<ItemStack>> wrap(Iterable<ItemStack> javaObject) {
    return get(ArmorClass.class).toLuaObject(javaObject);
  }

  public PatchedImmutableTable wrap(LivingEvent javaObject) {
    if (javaObject instanceof PlayerEvent) {
      wrap((PlayerEvent) javaObject);
    }
    return get(LivingEventClass.class).toLuaObject(javaObject);
  }

  public long wrap(long javaObject) {
    return javaObject;
  }

  public PatchedImmutableTable wrap(Material javaObject) {
    return get(MaterialClass.class).toLuaObject(javaObject);
  }

  public PatchedImmutableTable wrap(ServerChatEvent javaObject) {
    return get(ServerChatEventClass.class).toLuaObject(javaObject);
  }

  public PatchedImmutableTable wrap(PlayerEvent javaObject) {
    if (javaObject instanceof AnimationHandEvent) {
      wrap((AnimationHandEvent) javaObject);
    }
    if (javaObject instanceof ClickWindowEvent) {
      wrap((ClickWindowEvent) javaObject);
    }
    if (javaObject instanceof PlayerInteractEvent) {
      wrap((PlayerInteractEvent) javaObject);
    }
    return get(PlayerEventClass.class).toLuaObject(javaObject);
  }

  public PatchedImmutableTable wrap(PlayerInteractEvent javaObject) {
    if (javaObject instanceof RightClickBlock) {
      wrap((RightClickBlock) javaObject);
    }
    return get(PlayerInteractEventClass.class).toLuaObject(javaObject);
  }

  public PatchedImmutableTable wrap(RightClickBlock javaObject) {
    return get(RightClickBlockEventClass.class).toLuaObject(javaObject);
  }

  public long wrap(short javaObject) {
    return javaObject;
  }

  public DelegatingTable<? extends Spell> wrap(Spell javaObject) {
    return get(SpellClass.class).toLuaObject(javaObject);
  }

  public ByteString wrap(String javaObject) {
    return ByteString.of(javaObject);
  }

  public PatchedImmutableTable wrap(Vec3d javaObject) {
    return get(Vec3Class.class).toLuaObject(javaObject);
  }

  public PatchedImmutableTable wrap(WhisperEvent javaObject) {
    return get(WhisperEventClass.class).toLuaObject(javaObject);
  }

  public PatchedImmutableTable wrap(Vec3i javaObject) {
    return wrap(new Vec3d(javaObject));
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

  public PatchedImmutableTable wrapStrings(String[] javaObject) {
    PatchedImmutableTable.Builder builder = new PatchedImmutableTable.Builder();
    long idx = 0;
    for (String value : javaObject) {
      idx++;
      builder.add(idx, value);
    }
    return builder.build();
  }
}
