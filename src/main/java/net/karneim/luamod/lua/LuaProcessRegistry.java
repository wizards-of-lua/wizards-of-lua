package net.karneim.luamod.lua;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class LuaProcessRegistry {

  private final List<LuaProcessEntity> entities = new ArrayList<LuaProcessEntity>();

  public void register(LuaProcessEntity entity) {
    entities.add(entity);
  }

  public void unregister(LuaProcessEntity entity) {
    entities.remove(entity);
  }

  public Iterable<LuaProcessEntity> getAll() {
    return Iterables.unmodifiableIterable(entities);
  }

  public @Nullable LuaProcessEntity get(String name) {
    if (name == null) {
      return null;
    }
    for (LuaProcessEntity e : entities) {
      if (e.getName().equals(name)) {
        return e;
      }
    }
    return null;
  }

  public List<String> getNames() {
    List<String> result = new ArrayList<String>();
    for (LuaProcessEntity e : Lists.newArrayList(entities)) {
      result.add(e.getName());
    }
    return result;
  }

  public String list() {
    StringBuilder buf = new StringBuilder();
    for (LuaProcessEntity e : Lists.newArrayList(entities)) {
      if (buf.length() > 0) {
        buf.append("\n");
      }
      buf.append(e.getName());
    }
    return buf.toString();
  }

  public void killAll() {
    for (LuaProcessEntity e : Lists.newArrayList(entities)) {
      e.setDead();
    }
  }

  public void unpauseAll() {
    for (LuaProcessEntity e : Lists.newArrayList(entities)) {
      e.unpause();
    }
  }

  public void pauseAll() {
    for (LuaProcessEntity e : Lists.newArrayList(entities)) {
      e.pause();
    }
  }

  public boolean kill(String pid) {
    boolean result = false;
    for (LuaProcessEntity e : Lists.newArrayList(entities)) {
      if (pid.equals(e.getName())) {
        e.setDead();
        result = true;
      }
    }
    return result;
  }

  public boolean pause(String pid) {
    boolean result = false;
    for (LuaProcessEntity e : Lists.newArrayList(entities)) {
      if (pid.equals(e.getName())) {
        e.pause();
        result = true;
      }
    }
    return result;
  }

  public boolean unpause(String pid) {
    boolean result = false;
    for (LuaProcessEntity e : Lists.newArrayList(entities)) {
      if (pid.equals(e.getName())) {
        e.unpause();
        result = true;
      }
    }
    return result;
  }



}
