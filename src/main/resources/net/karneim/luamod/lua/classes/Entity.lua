Entity = {}

function Entity:new(o)
  local o = o or {}
  setmetatable(o, self)
  self.__index = self
  return o
end