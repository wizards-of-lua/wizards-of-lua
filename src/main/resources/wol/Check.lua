-- Library for checking preconditions 
-- wol.Check.lua

Check={}

-- Checks if obj is an instance of string. If not, a error is thrown.
function Check.isString(obj,i)
  local ok=type(obj)=="string"
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
function Check.isNumber(obj,i)
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

