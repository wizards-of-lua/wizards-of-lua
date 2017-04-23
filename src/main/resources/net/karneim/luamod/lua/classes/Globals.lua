-- Global Utility Functions 

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

function check.string(c,i)
  local t=type(c)
  if tonumber(i) then
    assert(t=="string", "bad argument #%d (string expected, got %s)",i,t)
  elseif type(i) == "string" then
    assert(t=="string", "bad argument '%s' (string expected, got %s)",i,t)
  else
    error("Illegal position argument for check call: %s",i)
  end
end

function check.number(n,i)
  if tonumber(i) then
    assert(tonumber(n),"bad argument #%d (number expected, got %s)",i,type(n))
  elseif type(i) == "string" then
    assert(tonumber(n),"bad argument '%s' (number expected, got %s)",i,type(n))
  else
    error("Illegal position argument for check call: %s",i)
  end
end

function check.class( obj, cls, i)
  local t=type( obj)
  local name = cls.__classname
  if tonumber( i) then
    assert( getmetatable( obj)==cls, "bad argument #%d (%s expected, got %s)", i, name, t)
  elseif type(i) == "string" then
    assert( getmetatable( obj)==cls, "bad argument '%s' (%s expected, got %s)", i, name, t)
  else
    error( "Illegal position argument for check call: %s", i)
  end
end

function class(name, base)
  check.string(name,1)
  --  create class table
  local c = {}
  c.__index = c
  c.__classname = name
  setmetatable(c,base)
  
  -- extend type function
  local type_ = type
  type = function(x)
    return getmetatable(x)==c and name or type_(x)
  end
  
  _G[name] = c
  return c
end

local function isempty(tbl)
  for k,v in pairs(tbl) do
    return false
  end
  return true
end

function table.format(tbl)
end

function str(obj)
  return inspect(obj)
end

function sleep(ticks)
  Runtime.sleep(ticks)
end
