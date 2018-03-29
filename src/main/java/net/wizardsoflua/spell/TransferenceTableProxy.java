package net.wizardsoflua.spell;

import static com.google.common.base.Preconditions.checkArgument;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.IllegalOperationAttemptException;
import net.wizardsoflua.lua.TransferenceProxyFactory;
import net.wizardsoflua.lua.classes.LuaClass;
import net.wizardsoflua.lua.classes.LuaClassLoader;

public class TransferenceTableProxy extends Table {
  private final Table delegate;
  private final LuaClassLoader delegateClassLoader;
  private final LuaClassLoader viewingClassLoader;
  private final TransferenceProxyFactory delegateFactory;
  private final TransferenceProxyFactory viewingFactory;

  public TransferenceTableProxy(Table delegate, LuaClassLoader delegateClassLoader,
      LuaClassLoader viewingClassLoader) {
    checkArgument(delegateClassLoader != viewingClassLoader,
        "Source and viewing LuaClassLoader are identical");
    this.delegate = delegate;
    this.delegateClassLoader = delegateClassLoader;
    this.viewingClassLoader = viewingClassLoader;
    delegateFactory = delegateClassLoader.getTransferenceProxyFactory();
    viewingFactory = viewingClassLoader.getTransferenceProxyFactory();
  }

  public Table getDelegate() {
    return delegate;
  }

  public LuaClassLoader getDelegateClassLoader() {
    return delegateClassLoader;
  }

  private Object transferToDelegate(Object luaObject) {
    return delegateFactory.getProxy(luaObject, viewingClassLoader);
  }

  private Object transferToViewer(Object luaObject) {
    return viewingFactory.getProxy(luaObject, delegateClassLoader);
  }

  @Override
  public Table getMetatable() {
    String className = delegateClassLoader.getTypes().getClassname(delegate);
    if (className != null) {
      LuaClass targetClass = viewingClassLoader.getLuaClassForName(className);
      if (targetClass != null) {
        return targetClass.getMetaTable();
      }
    }
    return null;
  }

  @Override
  public Table setMetatable(Table mt) {
    throw new IllegalOperationAttemptException(
        "attempt to set the metatable of a table from a different spell");
  }

  @Override
  public Object rawget(long idx) {
    Object result = delegate.rawget(idx);
    return transferToViewer(result);
  }

  @Override
  public Object rawget(Object key) {
    key = transferToDelegate(key);
    Object result = delegate.rawget(key);
    return transferToViewer(result);
  }

  @Override
  public void rawset(long idx, Object value) {
    value = transferToDelegate(value);
    delegate.rawset(idx, value);
  }

  @Override
  public void rawset(Object key, Object value) {
    key = transferToDelegate(key);
    value = transferToDelegate(value);
    delegate.rawset(key, value);
  }

  @Override
  public long rawlen() {
    return delegate.rawlen();
  }

  @Override
  public Object initialKey() {
    Object result = delegate.initialKey();
    return transferToViewer(result);
  }

  @Override
  public Object successorKeyOf(Object key) {
    key = transferToDelegate(key);
    Object result = delegate.successorKeyOf(key);
    return transferToViewer(result);
  }

  @Override
  protected void setMode(boolean weakKeys, boolean weakValues) {
    // no-op
  }
}
