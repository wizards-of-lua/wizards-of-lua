package net.karneim.luamod.lua;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class SpellRegistry {

  private final List<SpellEntity> entities = new ArrayList<SpellEntity>();

  private int spellCounter = 0;

  public void register(SpellEntity entity) {
    entity.setCustomNameTag("Spell-" + spellCounter++);
    entities.add(entity);
  }

  public void unregister(SpellEntity entity) {
    entities.remove(entity);
  }

  public Iterable<SpellEntity> getAll() {
    return Iterables.unmodifiableIterable(entities);
  }

  public @Nullable SpellEntity get(String name) {
    if (name == null) {
      return null;
    }
    for (SpellEntity e : entities) {
      if (e.getName().equals(name)) {
        return e;
      }
    }
    return null;
  }

  public List<String> getSpellIds() {
    List<String> result = new ArrayList<String>();
    for (SpellEntity e : Lists.newArrayList(entities)) {
      result.add(e.getName());
    }
    return result;
  }

  public String list() {
    StringBuilder buf = new StringBuilder();
    for (SpellEntity e : Lists.newArrayList(entities)) {
      if (buf.length() > 0) {
        buf.append("\n");
      }
      buf.append(e.getName());
      if (e.getCommand() != null) {
        buf.append(": ");
        buf.append(e.getCommand());
      }
    }
    return buf.toString();
  }

  public void breakAll() {
    for (SpellEntity e : Lists.newArrayList(entities)) {
      e.setDead();
    }
  }

  public void unpauseAll() {
    for (SpellEntity e : Lists.newArrayList(entities)) {
      e.unpause();
    }
  }

  public void pauseAll() {
    for (SpellEntity e : Lists.newArrayList(entities)) {
      e.pause();
    }
  }

  public boolean breakSpell(String spellId) {
    boolean result = false;
    for (SpellEntity e : Lists.newArrayList(entities)) {
      if (spellId.equals(e.getName())) {
        e.setDead();
        result = true;
      }
    }
    return result;
  }

  public boolean pauseSpell(String spellId) {
    boolean result = false;
    for (SpellEntity e : Lists.newArrayList(entities)) {
      if (spellId.equals(e.getName())) {
        e.pause();
        result = true;
      }
    }
    return result;
  }

  public boolean unpauseSpell(String spellId) {
    boolean result = false;
    for (SpellEntity e : Lists.newArrayList(entities)) {
      if (spellId.equals(e.getName())) {
        e.unpause();
        result = true;
      }
    }
    return result;
  }

}
