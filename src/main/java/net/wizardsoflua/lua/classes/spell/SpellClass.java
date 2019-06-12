package net.wizardsoflua.lua.classes.spell;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.auto.service.AutoService;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.lib.StringLib;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaFunctionDoc;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.block.LiveWolBlock;
import net.wizardsoflua.block.WolBlock;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.api.resource.LuaTypes;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.function.NamedFunctionAnyArg;
import net.wizardsoflua.lua.view.ViewFactory;
import net.wizardsoflua.spell.SpellEntity;

/**
 * The <span class="notranslate">Spell</span> is one of the main magic classes used in most known
 * spells. It is used to control the properties and the behaviour of the executed spell itself.
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = SpellClass.NAME, superClass = VirtualEntityClass.class)
@GenerateLuaClassTable(instance = SpellClass.Instance.class)
@GenerateLuaDoc(subtitle = "Aspects of an Active Spell")
public final class SpellClass extends BasicLuaClass<SpellEntity, SpellClass.Instance<SpellEntity>> {
  public static final String NAME = "Spell";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new SpellClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<SpellEntity>> toLuaInstance(SpellEntity javaInstance) {
    return new SpellClassInstanceTable<>(new Instance<>(javaInstance, injector), getTable(),
        converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends SpellEntity> extends VirtualEntityClass.Instance<D> {
    @Inject
    private ViewFactory viewFactory;

    public Instance(D delegate, Injector injector) {
      super(delegate, injector);
    }

    /**
     * The 'block' denotes the *block's state* at the spell's position. Use it to find out about
     * what material the block is constructed of, or in which direction it is facing.
     *
     * #### Example
     *
     * Printing the name of the block at the spell's position.
     *
     * <code>
     * print(spell.block.name)
     * </code>
     *
     * #### Example
     *
     * Inspecting the block at the spell's position.
     *
     * <code>
     * print(str(spell.block))
     * </code>
     *
     * #### Example
     *
     * Changing the block at the spell's position into dirt.
     *
     * <code>
     * spell.block = Blocks.get("dirt")
     * </code>
     *
     * #### Example
     *
     * Copying the block at the spell's position 10 times upwards.
     *
     * <code>
     * local copy = spell.block
     * for i=1,10 do
     *   spell:move("up")
     *   spell.block = copy
     * end
     * </code>
     *
     */
    @LuaProperty
    public WolBlock getBlock() {
      BlockPos pos = new BlockPos(delegate.getPositionVector());
      World world = delegate.getEntityWorld();
      return new LiveWolBlock(pos, world);
    }

    @LuaProperty
    public void setBlock(WolBlock block) {
      World world = delegate.getEntityWorld();
      BlockPos pos = new BlockPos(delegate.getPositionVector());
      block.setBlock(world, pos);
    }

    /**
     * The 'data' property is a spell-specific table that can hold custom key-value pairs. These
     * entries are modifiable not only by the owning spell itself but also by all other spells.
     * Therefore the data property is very well suited for exchanging information between spells.
     *
     * #### Example
     *
     * Storing a copy of the block at the spell's position into the 'block' entry of the spell's
     * data property.
     *
     * <code>
     * spell.data.block = spell.block:copy()
     * </code>
     *
     * #### Example
     *
     * Creating a spell called 'rain-spell' that is dropping a lot of copies of a specific item that
     * is defined in the 'item' entry of the spell's data property.
     *
     * <code>
     * spell.name = 'rain-spell'
     * spell.visible = true
     * while true do
     *   local item = spell.data.item
     *   if item then
     *     spell:dropItem(item)
     *   end
     *   local dx = math.random(-1,1)
     *   local dz = math.random(-1,1)
     *   spell.pos = spell.pos + Vec3(dx,0,dz)
     *   sleep(math.random(10,60))
     * end
     * </code>
     *
     * Finding the (first) spell called 'rain-spell' and setting the item that is 'raining' down.
     *
     * <code>
     * local otherSpellName = 'rain-spell'
     * local otherSpell = Spells.find({name=otherSpellName})[1]
     * otherSpell.data.item = Items.get('diamond_axe')
     * </code>
     *
     */
    @LuaProperty
    public Table getData() {
      return delegate.getData(viewFactory);
    }

    /**
     * The 'owner' is a reference to the [entity](/modules/entity) that owns this spell.
     *
     * In general, the owner is the same entity that has casted this spell directly using the
     * [/lua](/lua-command) command.
     *
     * In most circumstances this will be a [player](/modules/Player). But it can be any other
     * entity if the spell has been casted using the "/execute" command.
     *
     * For example, the following command
     *
     * <pre>
     * /execute @e[type=pig] ~ ~ ~ /lua spell.visible=true; sleep(20)
     * </pre>
     *
     * will cast a new spell on behalf of the next pig around. In this case that pig will be the
     * spell's owner.
     *
     * The onwer can also be <span class="notranslate">nil</span>. That's the case if the spell has
     * been casted from a command block or from the server console.
     *
     * If a spell casts another spell using [spell:execute()](/modules/Spell/#execute), then the
     * owner is inherited by the new spell.
     *
     * #### Example
     *
     * Printing the name of this spell's onwer.
     *
     * <code>
     * if spell.owner then
     *   print(spell.owner.name)
     * end
     * </code>
     */
    @LuaProperty
    public @Nullable Entity getOwner() {
      return delegate.getOwnerEntity();
    }

    /**
     * The 'sid' is the spell's numerical id.
     *
     * #### Example
     *
     * Breaking each spell in the range of 10 meters.
     *
     * <code>
     * found = Spells.find({maxradius=10})
     * for _,s in pairs(found) do
     *   spell:execute("wol spell break bySid %s", s.sid)
     * end
     * </code>
     */
    @LuaProperty
    public long getSid() {
      return delegate.getSid();
    }

    /**
     * The 'visible' property defines if this spell is visible for players.
     *
     * #### Example
     *
     * Making the spell visible.
     *
     * <code>
     * spell.visible = true
     * </code>
     *
     * #### Example
     *
     * Making the spell visible and moving it around in a circle.
     *
     * <code>
     * spell.visible = true
     * start = spell.pos
     * for a=0,math.pi*2,0.1 do
     *   z = math.sin(a)
     *   x = math.cos(a)
     *   r = 3
     *   spell.pos = start + Vec3(x,0,z) * r
     *   sleep(1)
     * end
     * </code>
     */
    @LuaProperty
    public boolean isVisible() {
      return delegate.isVisible();
    }

    @LuaProperty
    public void setVisible(boolean visible) {
      delegate.setVisible(visible);
    }

    /**
     * This function executes the given Minecraft command.
     *
     * A command can be specified with or without the leading slash '/' character.
     *
     * This function supports additional arguments which are 'formatted' into placeholders that must
     * be present in the command string. See
     * [`string.format()`](http://lua-users.org/wiki/StringLibraryTutorial) for more information.
     *
     * The command will be effective from this spell's position since this spell is the command's
     * source.
     *
     * If a [/lua](/lua-command) command is executed, then the new spell inherits this spell's
     * [owner](/modules/Spell#owner).
     *
     * This function returns a value greater 0 if it was successful, or 0 if it wasn't.
     *
     * The actual return value is command specific. For example:
     *
     * *the "give" command will return the number of items that have been given to the recipient
     *
     * *the "fill" command will return the number of blocks that have been actually changed
     *
     * *the "execute" command will return the number of successful invocations on its targets
     *
     * #### Example
     *
     * Setting the game time to "day".
     *
     * <code>
     * spell:execute("/time set day")
     * </code>
     *
     * #### Example
     *
     * Letting the current spell say "hello".
     *
     * <code>
     * spell:execute("/say hello")
     * </code>
     *
     * #### Example
     *
     * Letting the player "mickkay" say "hello":
     *
     * <code>
     * spell:execute("/execute mickkay ~ ~ ~ say hello")
     * </code>
     *
     * #### Example
     *
     * Spawning a zombie at the spell's current location.
     *
     * <code>
     * spell:execute("/summon zombie ~ ~ ~")
     * </code>
     *
     * #### Example
     *
     * Spawning some smoke particles at the spell's current location using a command placeholder.
     *
     * <code>
     * local particle = "smoke"
     * spell:execute("/particle %s ~ ~ ~ 0 0 0 0 0", particle)
     * </code>
     *
     * #### Example
     *
     * Building a wall by concurrently casting serveral spells, each building a pillar.
     *
     * <code>
     * for x=1,20 do
     *   spell:execute([[/lua
     *     for i=1,5 do
     *       spell.block = Blocks.get("stone")
     *       sleep(1)
     *       spell:move("up")
     *     end
     *   ]])
     *   spell:move("north")
     * end
     * </code>
     *
     * #### Example
     *
     * Drawing a circle of black smoke around the spell's position with a radius of 1.4 meters
     *
     * <code>
     * start = spell.pos
     * for a=0,math.pi*2,0.1 do
     *   z = math.sin(a)
     *   x = math.cos(a)
     *   y = 0.6
     *   r = 1.4
     *   spell.pos = start + Vec3(x,y,z) * r
     *   spell:execute("/particle largesmoke ~ ~ ~ 0 0 0 0 1")
     * end
     * </code>
     *
     * #### Example
     *
     * Removing every diamond axe from all players, then printing the number of removed items.
     *
     * <code>
     * local r=spell:execute([[ /clear @a minecraft:diamond_axe ]])
     * print(r)
     * </code>
     *
     */
    @LuaFunction(name = ExecuteFunction.NAME)
    @LuaFunctionDoc(returnType = LuaTypes.NUMBER, args = {"command", "..."})
    static class ExecuteFunction extends NamedFunctionAnyArg {
      public static final String NAME = "execute";
      private final SpellClass luaClass;

      public ExecuteFunction(SpellClass luaClass) {
        this.luaClass = requireNonNull(luaClass, "luaClass == null!");
      }

      @Override
      public String getName() {
        return NAME;
      }

      @Override
      public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
        Object arg1 = getArg(1, args);
        Instance<?> self = luaClass.converters.toJava(Instance.class, arg1, 1, "self", NAME);
        String command;
        if (args.length > 2) { // format the command
          Object[] format = Arrays.copyOfRange(args, 1, args.length);
          StringLib.format().invoke(context, format);
          command = String.valueOf(context.getReturnBuffer().get0());
        } else {
          Object arg2 = getArg(2, args);
          command = luaClass.converters.toJava(String.class, arg2, 2, "command", getName());
        }
        int result = self.execute(command);
        context.getReturnBuffer().setTo(result);
      }

      private Object getArg(int i, Object[] args) {
        return args.length < i ? null : args[i - 1];
      }
    }

    public int execute(String command) {
      return delegate.execute(command);
    }
  }
}
