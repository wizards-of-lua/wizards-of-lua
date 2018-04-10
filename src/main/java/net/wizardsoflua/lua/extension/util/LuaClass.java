package net.wizardsoflua.lua.extension.util;

import net.wizardsoflua.lua.extension.spi.ConverterExtension;

public interface LuaClass<J, L> extends LuaTableExtension, ConverterExtension<J, L> {
}
