package net.wizardsoflua.lua.converter;

import net.wizardsoflua.extension.spell.spi.LuaConverter;

public abstract class TypeTokenLuaConverter<J, L> extends TypeTokenLuaToJavaConverter<J, L>
    implements LuaConverter<J, L> {
}
