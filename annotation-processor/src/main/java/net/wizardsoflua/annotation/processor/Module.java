package net.wizardsoflua.annotation.processor;

public class Module {
  private final String name;
  private final Class<?> superClass;

  public Module(String name, Class<?> superClass) {
    this.name = name;
    this.superClass = superClass;
  }


}
