package net.karneim.luamod.lua.classes;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.karneim.luamod.cursor.Spell;
import net.karneim.luamod.lua.classes.entity.EntityClass;
import net.karneim.luamod.lua.classes.entity.item.EntityItemClass;
import net.karneim.luamod.lua.classes.event.EventClass;
import net.karneim.luamod.lua.classes.event.ServerChatEventClass;
import net.karneim.luamod.lua.classes.event.brewing.PotionBrewEventClass;
import net.karneim.luamod.lua.classes.event.brewing.PotionBrewPostEventClass;
import net.karneim.luamod.lua.classes.event.brewing.PotionBrewPreEventClass;
import net.karneim.luamod.lua.classes.event.entity.EntityEventClass;
import net.karneim.luamod.lua.classes.event.entity.item.ItemEventClass;
import net.karneim.luamod.lua.classes.event.entity.item.ItemExpireEventClass;
import net.karneim.luamod.lua.classes.event.entity.item.ItemTossEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingAttackEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingDeathEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingDropsEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingEntityUseItemEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingEntityUseItemFinishEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingEntityUseItemStartEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingEntityUseItemStopEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingEntityUseItemTickEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.LeftClickBlockEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.PlayerEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.PlayerInteractEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.RightClickBlockEventClass;
import net.karneim.luamod.lua.classes.event.game.PlayerGameEventClass;
import net.karneim.luamod.lua.classes.event.game.PlayerLoggedInEventClass;
import net.karneim.luamod.lua.classes.event.game.PlayerLoggedOutEventClass;
import net.karneim.luamod.lua.classes.event.game.PlayerRespawnEventClass;
import net.karneim.luamod.lua.classes.event.wol.AnimationHandEventClass;
import net.karneim.luamod.lua.classes.event.wol.ClickWindowEventClass;
import net.karneim.luamod.lua.classes.event.wol.CustomLuaEventClass;
import net.karneim.luamod.lua.classes.event.wol.WhisperEventClass;
import net.karneim.luamod.lua.event.AnimationHandEvent;
import net.karneim.luamod.lua.event.ClickWindowEvent;
import net.karneim.luamod.lua.event.CustomLuaEvent;
import net.karneim.luamod.lua.event.WhisperEvent;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.wrapper.ModifiableArrayWrapper;
import net.karneim.luamod.lua.wrapper.UnmodifiableIterableWrapper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
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

  public @Nullable DelegatingTable<? extends AnimationHandEvent> wrap(
      @Nullable AnimationHandEvent javaObject) {
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

  public @Nullable DelegatingTable<? extends ClickWindowEvent> wrap(
      @Nullable ClickWindowEvent javaObject) {
    return get(ClickWindowEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends CustomLuaEvent> wrap(
      @Nullable CustomLuaEvent javaObject) {
    return get(CustomLuaEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends DamageSource> wrap(@Nullable DamageSource javaObject) {
    return get(DamageSourceClass.class).getLuaObjectNullable(javaObject);
  }

  public double wrap(double javaObject) {
    return javaObject;
  }

  public @Nullable DelegatingTable<? extends Entity> wrap(@Nullable Entity javaObject) {
    if (javaObject instanceof EntityItem) {
      return wrap((EntityItem) javaObject);
    }
    if (javaObject instanceof EntityLivingBase) {
      return wrap((EntityLivingBase) javaObject);
    }
    return get(EntityClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends EntityEvent> wrap(@Nullable EntityEvent javaObject) {
    if (javaObject instanceof ItemEvent) {
      return wrap((ItemEvent) javaObject);
    }
    if (javaObject instanceof LivingEvent) {
      return wrap((LivingEvent) javaObject);
    }
    return get(EntityEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends EntityItem> wrap(@Nullable EntityItem javaObject) {
    return get(EntityItemClass.class).getLuaObjectNullable(javaObject);
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

  public @Nullable DelegatingTable<? extends Event> wrap(@Nullable Event javaObject) {
    if (javaObject instanceof CustomLuaEvent) {
      return wrap((CustomLuaEvent) javaObject);
    }
    if (javaObject instanceof EntityEvent) {
      return wrap((EntityEvent) javaObject);
    }
    if (javaObject instanceof PotionBrewEvent) {
      return wrap((PotionBrewEvent) javaObject);
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

  public @Nullable DelegatingTable<? extends IBlockState> wrap(@Nullable IBlockState javaObject) {
    return get(BlockStateClass.class).getLuaObjectNullable(javaObject);
  }

  public long wrap(int javaObject) {
    return javaObject;
  }

  public @Nullable DelegatingTable<? extends ItemEvent> wrap(@Nullable ItemEvent javaObject) {
    if (javaObject instanceof ItemExpireEvent) {
      return wrap((ItemExpireEvent) javaObject);
    }
    if (javaObject instanceof ItemTossEvent) {
      return wrap((ItemTossEvent) javaObject);
    }
    return get(ItemEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends ItemExpireEvent> wrap(
      @Nullable ItemExpireEvent javaObject) {
    return get(ItemExpireEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends ItemStack> wrap(@Nullable ItemStack javaObject) {
    return get(ItemStackClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends ItemTossEvent> wrap(
      @Nullable ItemTossEvent javaObject) {
    return get(ItemTossEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends LeftClickBlock> wrap(
      @Nullable LeftClickBlock javaObject) {
    return get(LeftClickBlockEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends LivingAttackEvent> wrap(
      @Nullable LivingAttackEvent javaObject) {
    return get(LivingAttackEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends LivingDeathEvent> wrap(
      @Nullable LivingDeathEvent javaObject) {
    return get(LivingDeathEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends LivingDropsEvent> wrap(
      @Nullable LivingDropsEvent javaObject) {
    return get(LivingDropsEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends LivingEntityUseItemEvent> wrap(
      @Nullable LivingEntityUseItemEvent javaObject) {
    if (javaObject instanceof LivingEntityUseItemEvent.Finish) {
      return wrap((LivingEntityUseItemEvent.Finish) javaObject);
    }
    if (javaObject instanceof LivingEntityUseItemEvent.Start) {
      return wrap((LivingEntityUseItemEvent.Finish) javaObject);
    }
    if (javaObject instanceof LivingEntityUseItemEvent.Stop) {
      return wrap((LivingEntityUseItemEvent.Finish) javaObject);
    }
    if (javaObject instanceof LivingEntityUseItemEvent.Tick) {
      return wrap((LivingEntityUseItemEvent.Finish) javaObject);
    }
    return get(LivingEntityUseItemEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends LivingEntityUseItemEvent.Finish> wrap(
      @Nullable LivingEntityUseItemEvent.Finish javaObject) {
    return get(LivingEntityUseItemFinishEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends LivingEntityUseItemEvent.Start> wrap(
      @Nullable LivingEntityUseItemEvent.Start javaObject) {
    return get(LivingEntityUseItemStartEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends LivingEntityUseItemEvent.Stop> wrap(
      @Nullable LivingEntityUseItemEvent.Stop javaObject) {
    return get(LivingEntityUseItemStopEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends LivingEntityUseItemEvent.Tick> wrap(
      @Nullable LivingEntityUseItemEvent.Tick javaObject) {
    return get(LivingEntityUseItemTickEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends LivingEvent> wrap(@Nullable LivingEvent javaObject) {
    if (javaObject instanceof LivingAttackEvent) {
      return wrap((LivingAttackEvent) javaObject);
    }
    if (javaObject instanceof LivingDeathEvent) {
      return wrap((LivingDeathEvent) javaObject);
    }
    if (javaObject instanceof LivingDropsEvent) {
      return wrap((LivingDropsEvent) javaObject);
    }
    if (javaObject instanceof LivingEntityUseItemEvent) {
      return wrap((LivingEntityUseItemEvent) javaObject);
    }
    if (javaObject instanceof PlayerEvent) {
      return wrap((PlayerEvent) javaObject);
    }
    return get(LivingEventClass.class).getLuaObjectNullable(javaObject);
  }

  public long wrap(long javaObject) {
    return javaObject;
  }

  public @Nullable DelegatingTable<? extends Material> wrap(@Nullable Material javaObject) {
    return get(MaterialClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends net.minecraftforge.fml.common.gameevent.PlayerEvent> wrap(
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

  public @Nullable DelegatingTable<? extends PlayerEvent> wrap(@Nullable PlayerEvent javaObject) {
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

  public @Nullable DelegatingTable<? extends PlayerInteractEvent> wrap(
      @Nullable PlayerInteractEvent javaObject) {
    if (javaObject instanceof LeftClickBlock) {
      return wrap((LeftClickBlock) javaObject);
    }
    if (javaObject instanceof RightClickBlock) {
      return wrap((RightClickBlock) javaObject);
    }
    return get(PlayerInteractEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends PlayerLoggedInEvent> wrap(
      @Nullable PlayerLoggedInEvent javaObject) {
    return get(PlayerLoggedInEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends PlayerLoggedOutEvent> wrap(
      @Nullable PlayerLoggedOutEvent javaObject) {
    return get(PlayerLoggedOutEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends PlayerRespawnEvent> wrap(
      @Nullable PlayerRespawnEvent javaObject) {
    return get(PlayerRespawnEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends PotionBrewEvent> wrap(
      @Nullable PotionBrewEvent javaObject) {
    if (javaObject instanceof PotionBrewEvent.Pre) {
      return wrap((PotionBrewEvent.Pre) javaObject);
    }
    if (javaObject instanceof PotionBrewEvent.Post) {
      return wrap((PotionBrewEvent.Post) javaObject);
    }
    return get(PotionBrewEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends PotionBrewEvent.Post> wrap(
      @Nullable PotionBrewEvent.Post javaObject) {
    return get(PotionBrewPostEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends PotionBrewEvent.Pre> wrap(
      @Nullable PotionBrewEvent.Pre javaObject) {
    return get(PotionBrewPreEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends RightClickBlock> wrap(
      @Nullable RightClickBlock javaObject) {
    return get(RightClickBlockEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends ServerChatEvent> wrap(
      @Nullable ServerChatEvent javaObject) {
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

  public @Nullable DelegatingTable<? extends Vec3d> wrap(@Nullable Vec3d javaObject) {
    return get(Vec3Class.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends Vec3d> wrap(@Nullable Vec3i javaObject) {
    return javaObject == null ? null : wrap(new Vec3d(javaObject));
  }

  public @Nullable DelegatingTable<? extends WhisperEvent> wrap(@Nullable WhisperEvent javaObject) {
    return get(WhisperEventClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends Iterable<ItemStack>> wrapArmor(
      @Nullable Iterable<ItemStack> javaObject) {
    return get(ArmorClass.class).getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends Iterable<String>> wrapStrings(
      @Nullable Iterable<String> javaObject) {
    if (javaObject == null) {
      return null;
    }
    UnmodifiableIterableWrapper<String, ByteString> wrapper =
        new UnmodifiableIterableWrapper<>(j -> ByteString.of(j));
    return wrapper.createLuaObject(javaObject);
  }

  public @Nullable DelegatingTable<? extends String[]> wrapStrings(@Nullable String[] javaObject) {
    if (javaObject == null) {
      return null;
    }
    ModifiableArrayWrapper<String, ByteString> wrapper =
        new ModifiableArrayWrapper<>(ByteString.class, j -> ByteString.of(j), l -> l.decode());
    return wrapper.createLuaObject(javaObject);
  }
}
