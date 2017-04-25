package net.karneim.luamod.lua.classes;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.karneim.luamod.cursor.Spell;
import net.karneim.luamod.lua.classes.event.AnimationHandEventClass;
import net.karneim.luamod.lua.classes.event.ClickWindowEventClass;
import net.karneim.luamod.lua.classes.event.CustomLuaEventClass;
import net.karneim.luamod.lua.classes.event.EntityEventClass;
import net.karneim.luamod.lua.classes.event.EventClass;
import net.karneim.luamod.lua.classes.event.LeftClickBlockEventClass;
import net.karneim.luamod.lua.classes.event.LivingEventClass;
import net.karneim.luamod.lua.classes.event.PlayerEventClass;
import net.karneim.luamod.lua.classes.event.PlayerGameEventClass;
import net.karneim.luamod.lua.classes.event.PlayerInteractEventClass;
import net.karneim.luamod.lua.classes.event.PlayerLoggedInEventClass;
import net.karneim.luamod.lua.classes.event.PlayerLoggedOutEventClass;
import net.karneim.luamod.lua.classes.event.PlayerRespawnEventClass;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;

public class LuaTypesRepo {

  private final Map<String, LuaClass> types = new HashMap<>();
  private final Table env;

  public LuaTypesRepo(Table env) {
    this.env = checkNotNull(env);
  }

  public <T extends LuaClass> T get(Class<T> cls) {
    String moduleName = LuaClass.getModuleNameOf(cls);
    LuaClass luaClass = get(moduleName);
    return cls.cast(luaClass);
  }

  public LuaClass get(String name) {
    return types.get(name);
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

  public @Nullable PatchedImmutableTable wrap(@Nullable AnimationHandEvent javaObject) {
    return get(AnimationHandEventClass.class).getLuaObjectNullable(javaObject);
  }

  public boolean wrap(boolean javaObject) {
    return javaObject;
  }

  public long wrap(byte javaObject) {
    return javaObject;
  }

  public @Nullable ByteString wrap(@Nullable ByteString javaObject) {
    return javaObject;
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable ClickWindowEvent javaObject) {
    return get(ClickWindowEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable CustomLuaEvent javaObject) {
    return get(CustomLuaEventClass.class).getLuaObjectNullable(javaObject);
  }

  public double wrap(double javaObject) {
    return javaObject;
  }

  public @Nullable DelegatingTable<? extends Entity> wrap(@Nullable Entity javaObject) {
    if (javaObject instanceof EntityLivingBase) {
      return wrap((EntityLivingBase) javaObject);
    }
    return get(EntityClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable EntityEvent javaObject) {
    if (javaObject instanceof LivingEvent) {
      return wrap((LivingEvent) javaObject);
    }
    return get(EntityEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends EntityLiving> wrap(@Nullable EntityLiving javaObject) {
    return get(EntityLivingClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends EntityLivingBase> wrap(
      @Nullable EntityLivingBase javaObject) {
    if (javaObject instanceof EntityLiving) {
      return wrap((EntityLiving) javaObject);
    }
    if (javaObject instanceof EntityPlayer) {
      return wrap((EntityPlayer) javaObject);
    }
    return get(EntityLivingBaseClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends EntityPlayer> wrap(@Nullable EntityPlayer javaObject) {
    return get(EntityPlayerClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable ByteString wrap(@Nullable Enum<?> javaObject) {
    return javaObject == null ? null : ByteString.of(javaObject.name());
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable Event javaObject) {
    if (javaObject instanceof CustomLuaEvent) {
      return wrap((CustomLuaEvent) javaObject);
    }
    if (javaObject instanceof EntityEvent) {
      return wrap((EntityEvent) javaObject);
    }
    if (javaObject instanceof ServerChatEvent) {
      return wrap((ServerChatEvent) javaObject);
    }
    if (javaObject instanceof WhisperEvent) {
      return wrap((WhisperEvent) javaObject);
    }
    return get(EventClass.class).getLuaObjectNullable(javaObject);
  }

  public double wrap(float javaObject) {
    return javaObject;
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable IBlockState javaObject) {
    return get(BlockStateClass.class).getLuaObjectNullable(javaObject);
  }

  public long wrap(int javaObject) {
    return javaObject;
  }

  public @Nullable DelegatingTable<? extends ItemStack> wrap(@Nullable ItemStack javaObject) {
    return get(ItemStackClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends Iterable<ItemStack>> wrap(
      @Nullable Iterable<ItemStack> javaObject) {
    return get(ArmorClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable LeftClickBlock javaObject) {
    return get(LeftClickBlockEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable LivingEvent javaObject) {
    if (javaObject instanceof PlayerEvent) {
      return wrap((PlayerEvent) javaObject);
    }
    return get(LivingEventClass.class).getLuaObjectNullable(javaObject);
  }

  public long wrap(long javaObject) {
    return javaObject;
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable Material javaObject) {
    return get(MaterialClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable PatchedImmutableTable wrap(
      @Nullable net.minecraftforge.fml.common.gameevent.PlayerEvent javaObject) {
    if (javaObject instanceof PlayerLoggedInEvent) {
      return wrap((PlayerLoggedInEvent) javaObject);
    }
    if (javaObject instanceof PlayerLoggedOutEvent) {
      return wrap((PlayerLoggedOutEvent) javaObject);
    }
    if (javaObject instanceof PlayerRespawnEvent) {
      return wrap((PlayerRespawnEvent) javaObject);
    }
    return get(PlayerGameEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable PlayerEvent javaObject) {
    if (javaObject instanceof AnimationHandEvent) {
      return wrap((AnimationHandEvent) javaObject);
    }
    if (javaObject instanceof ClickWindowEvent) {
      return wrap((ClickWindowEvent) javaObject);
    }
    if (javaObject instanceof PlayerInteractEvent) {
      return wrap((PlayerInteractEvent) javaObject);
    }
    return get(PlayerEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable PlayerInteractEvent javaObject) {
    if (javaObject instanceof LeftClickBlock) {
      return wrap((LeftClickBlock) javaObject);
    }
    if (javaObject instanceof RightClickBlock) {
      return wrap((RightClickBlock) javaObject);
    }
    return get(PlayerInteractEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable PlayerLoggedInEvent javaObject) {
    return get(PlayerLoggedInEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable PlayerLoggedOutEvent javaObject) {
    return get(PlayerLoggedOutEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable PlayerRespawnEvent javaObject) {
    return get(PlayerRespawnEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable RightClickBlock javaObject) {
    return get(RightClickBlockEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable ServerChatEvent javaObject) {
    return get(ServerChatEventClass.class).getLuaObjectNullable(javaObject);
  }

  public long wrap(short javaObject) {
    return javaObject;
  }

  public @Nullable DelegatingTable<? extends Spell> wrap(@Nullable Spell javaObject) {
    return get(SpellClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable ByteString wrap(@Nullable String javaObject) {
    return javaObject == null ? null : ByteString.of(javaObject);
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable Vec3d javaObject) {
    return get(Vec3Class.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable Vec3i javaObject) {
    return javaObject == null ? null : wrap(new Vec3d(javaObject));
  }

  public @Nullable PatchedImmutableTable wrap(@Nullable WhisperEvent javaObject) {
    return get(WhisperEventClass.class).getLuaObjectNullable(javaObject);
  }

  public PatchedImmutableTable wrapStrings(Iterable<String> javaObject) {
    if (javaObject == null) {
      return null;
    }
    PatchedImmutableTable.Builder builder = new PatchedImmutableTable.Builder();
    long idx = 0;
    for (String value : javaObject) {
      idx++;
      builder.add(idx, value);
    }
    return builder.build();
  }

  public PatchedImmutableTable wrapStrings(String[] javaObject) {
    if (javaObject == null) {
      return null;
    }
    PatchedImmutableTable.Builder builder = new PatchedImmutableTable.Builder();
    long idx = 0;
    for (String value : javaObject) {
      idx++;
      builder.add(idx, value);
    }
    return builder.build();
  }
}
