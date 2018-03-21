package net.wizardsoflua.spell;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.wizardsoflua.lua.InstanceReceiver;
import net.wizardsoflua.lua.classes.LuaClassLoader;

public class Specifics extends DefaultTable {
  private final Cache<LuaClassLoader, View> cache =
      CacheBuilder.newBuilder().weakKeys().softValues().build();
  private final LuaClassLoader classLoader;
  private final InstanceReceiver owner;

  public Specifics(LuaClassLoader classLoader) {
    this.classLoader = checkNotNull(classLoader, "classLoader == null!");
    owner = classLoader.getInstanceReceiver();
  }

  public Table getView(LuaClassLoader viewingClassLoader) {
    if (classLoader == viewingClassLoader) {
      return this;
    }
    return cache.asMap().computeIfAbsent(viewingClassLoader, View::new);
  }

  public class View extends Table {
    private LuaClassLoader viewingClassLoader;
    private final InstanceReceiver viewer;

    public View(LuaClassLoader viewingClassLoader) {
      this.viewingClassLoader = checkNotNull(viewingClassLoader, "viewingClassLoader == null!");
      viewer = viewingClassLoader.getInstanceReceiver();
    }

    private Object asOwner(Object luaObject) {
      return owner.receive(luaObject, viewingClassLoader);
    }

    private Object asViewer(Object luaObject) {
      return viewer.receive(luaObject, classLoader);
    }

    @Override
    public Object rawget(Object key) {
      key = asOwner(key);
      Object result = Specifics.this.rawget(key);
      return asViewer(result);
    }

    @Override
    public void rawset(Object key, Object value) {
      key = asOwner(key);
      value = asOwner(value);
      Specifics.this.rawset(key, value);
    }

    @Override
    public Object initialKey() {
      Object result = Specifics.this.initialKey();
      return asViewer(result);
    }

    @Override
    public Object successorKeyOf(Object key) {
      key = asOwner(key);
      Object result = Specifics.this.successorKeyOf(key);
      return asViewer(result);
    }

    @Override
    protected void setMode(boolean weakKeys, boolean weakValues) {
      Specifics.this.setMode(weakKeys, weakValues);
    }
  }
}
