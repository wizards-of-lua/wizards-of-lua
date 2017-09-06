package net.wizardsoflua.lua.classes.entity;

import static java.lang.String.format;

import net.minecraft.entity.player.EntityPlayer;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.lib.BasicLib;
import net.sandius.rembulan.runtime.AbstractFunctionAnyArg;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.module.types.Terms;

public class PlayerClass {
  public static final String METATABLE_NAME = "Player";

  private final Converters converters;
  private final Table metatable;

  public PlayerClass(Converters converters) {
    this.converters = converters;
    // TODO do declaration outside this class
    this.metatable = converters.getTypes().declare(METATABLE_NAME, EntityClass.METATABLE_NAME);
    metatable.rawset("putNbt", new UnsupportedFunction("putNbt", METATABLE_NAME));
  }

  public Table toLua(EntityPlayer delegate) {
    return new Proxy(converters, metatable, delegate);
  }

  public class Proxy extends EntityClass.Proxy {

    private final EntityPlayer delegate;

    public Proxy(Converters converters, Table metatable, EntityPlayer delegate) {
      super(converters, metatable, delegate);
      this.delegate = delegate;

      // Overwrite name, since player names can't be changed
      addReadOnly("name", this::getName);
    }

  }

  private class UnsupportedFunction extends AbstractFunctionAnyArg {
    private final String name;
    private final String metatableName;

    public UnsupportedFunction(String name, String metatableName) {
      this.name = name;
      this.metatableName = metatableName;
    }

    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      converters.getTypes().checkAssignable(metatableName, args[0], Terms.MANDATORY);
      BasicLib.error().invoke(context,
          format("%s not supported for class %s", name, metatableName));
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }

  }
}
