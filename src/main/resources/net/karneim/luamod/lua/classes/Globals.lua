-- Global Utility Functions 

local error_=error
function error(fmt,...)  error_(string.format(fmt,...)) end

local assert_=assert
function assert(b,fmt,...) return fmt and assert_(b,string.format(fmt,...)) or assert_(b) end

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

--function str(obj,vis,indent)  
--  vis = vis or {}
--  indent = indent or ""
--  if obj == nil then
--    return "nil"
--  end
--  if type__(obj) == "table" then
--    if not isempty(obj) and vis[obj] == 1 then
--      return "<skipping endless recursion>"
--    end
--    vis[obj] = 1
--  end
--
--  if type__(obj)=="table" then
--    local res = "{"
--    for k,v in pairs(obj) do
--      if #res > 1 then
--        res = res..",\n "..indent
--      end
--      res = res..k.."="..str(v,vis,indent.."  ")
--    end
--    res = res.."\n"..indent.."}"
--    return res
--  elseif type(obj)=="boolean" then
--    return tostring(obj)
--  elseif type(obj)=="number" then
--    return obj
--  elseif type(obj)=="string" then
--    return "'"..obj.."'"
--  else
--    return type(obj)
--  end
--end

function str(obj)
  return inspect(obj)
end