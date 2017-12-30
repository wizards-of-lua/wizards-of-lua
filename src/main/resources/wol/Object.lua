-- Lua Module for the Object class

if Types==nil then
  error("Missing Types module")
end

function Object:isInstanceOf(cls)
  return Types.instanceOf(cls, self)
end
