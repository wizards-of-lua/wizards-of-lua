-- Global Utility Functions
-- net.wizardsoflua.lua.modules.Globals.lua

require "net.wizardsoflua.lua.modules.Check"
inspect = require "net.wizardsoflua.lua.modules.inspect"

-- Ensure that Types module is loaded
if Types==nil then
  error("Missing Types module")
end

function sleep(ticks)
  Runtime.sleep(ticks)
end

-- TODO do we need this? if so, why?
-- function table.format(tbl)
-- end

function str(obj)
  return inspect(obj,{metatables=false})
end

local error_ = error
function error( fmt, ...)
  error_( string.format( fmt, ...))
end

local assert_ = assert
function assert(b, fmt, ...)
  return fmt and assert_( b, string.format( fmt, ...)) or assert_( b)
end

-- Overwrite the type() function so that it can handle registered classes
local type_ = type
function type(obj)
  local result = Types.getTypename(obj)
  return result or type_(obj)
end

-- Returns true if obj is an instance of the given class
function instanceOf(cls, obj)
  return Types.instanceOf(cls, obj);
end

-- Declare a new class with given name and optional superclass
function declare(name, base)
  return Types.declare(name, base)
end
