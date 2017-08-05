-- Global Utility Functions
-- net.wizardsoflua.lua.modules.Globals.lua

function sleep(ticks)
  Runtime.sleep(ticks)
end

-- TODO do we need this? if so, why?
-- function table.format(tbl)
-- end

function str(obj)
  return inspect(obj)
end

local error_ = error
function error( fmt, ...)
  error_( string.format( fmt, ...))
end

local assert_ = assert
function assert(b, fmt, ...)
  return fmt and assert_( b, string.format( fmt, ...)) or assert_( b)
end

local type__=type

check={}

-- Checks if obj is an instance of string. If not, a error is thrown.
function check.string(obj,i)
  local ok=instanceOf(string,obj)
  if i==nil then
    assert(ok, "bad argument (string expected, got %s)", type(obj))
  elseif tonumber(i) then
    assert(ok, "bad argument #%d (string expected, got %s)",i,type(obj))
  elseif type(i) == "string" then
    assert(ok, "bad argument '%s' (string expected, got %s)",i,type(obj))
  else
    error("Illegal position argument for check call: %s",i)
  end
end

-- Checks if obj is an instance of number. If not, a error is thrown.
function check.number(obj,i)
  local ok=tonumber(obj)
  if i==nil then
    assert(ok, "bad argument (number expected, got %s)", type(obj))
  elseif tonumber(i) then
    assert(ok,"bad argument #%d (number expected, got %s)",i,type(obj))
  elseif type(i) == "string" then
    assert(ok,"bad argument '%s' (number expected, got %s)",i,type(obj))
  else
    error("Illegal position argument for check call: %s",i)
  end
end

-- TODO add generic check function that can check an agrument for any specific class



-- List of all registered classes
local classes = {}

-- Overwrite the type() function so that it can handle registered classes
local type_ = type
function type(obj)
  local mt = getmetatable(obj)
  local result = classes[mt]
  return result or type_(obj)
end

-- Returns true if obj is an instance of the given class
function instanceOf(cls, obj)
  if cls == string and type(obj) == "string" then
    return true
  end
  --if cls == table and type(obj) == "table" then
  --  return true
  --end
  local mt = getmetatable(obj)
  return mt ~= nil and (mt == cls or instanceOf(cls, mt))
end

-- Register a new class with given name and optional superclass
function class(name, base)
  check.string(name, 1)

  assert( _G[name] == nil, "bad argument #%d (a global variable with name '%s' is already defined)", 1, name)

  --  create class table
  local c = {}
  c.__index = c
  c.__classname = name
  setmetatable(c, base)

  -- add to classes
  classes[c] = name

  _G[name] = c
  return c
end
