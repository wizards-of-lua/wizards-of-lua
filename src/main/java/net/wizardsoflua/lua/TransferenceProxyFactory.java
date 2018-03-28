package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction0;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.IllegalOperationAttemptException;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.common.LuaInstanceProxy;
import net.wizardsoflua.lua.module.time.TimeModule;
import net.wizardsoflua.spell.TransferenceTableProxy;

public class TransferenceProxyFactory {
  /**
   * We don't want wizards to call functions that were defined in different spells, because these
   * functions would be executed using the env of the declaring spell. This could cause unexpected
   * behaviour, especially when interacting with the {@link TimeModule}.
   */
  private static final LuaFunction FUNCTION_PROXY = new AbstractFunction0() {
    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      throw new IllegalOperationAttemptException(
          "attempt to call a function from a different spell");
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  };
  private final Cache<Table, Table> cache = CacheBuilder.newBuilder().weakKeys().build();
  private final LuaClassLoader classLoader;

  public TransferenceProxyFactory(LuaClassLoader classLoader) {
    this.classLoader = requireNonNull(classLoader, "classLoader == null!");
  }

  public Object getProxy(Object luaObject, LuaClassLoader sourceClassLoader) {
    checkArgument(sourceClassLoader != classLoader,
        "Source and viewing LuaClassLoader are identical");
    if (luaObject instanceof LuaFunction) {
      return FUNCTION_PROXY;
    }
    if (luaObject instanceof Table) {
      if (luaObject instanceof LuaInstanceProxy) {
        LuaInstanceProxy<?> instance = (LuaInstanceProxy<?>) luaObject;
        Object delegate = instance.getDelegate();
        return classLoader.getConverters().toLua(delegate);
      }
      if (luaObject instanceof TransferenceTableProxy) {
        TransferenceTableProxy proxy = (TransferenceTableProxy) luaObject;
        luaObject = proxy.getDelegate();
        sourceClassLoader = proxy.getDelegateClassLoader();
        if (sourceClassLoader == classLoader) {
          return luaObject;
        }
      }
      LuaClassLoader delegateClassLoader = sourceClassLoader;
      return cache.asMap().computeIfAbsent((Table) luaObject,
          delegate -> new TransferenceTableProxy(delegate, delegateClassLoader, classLoader));
    }
    return luaObject;
  }
}
