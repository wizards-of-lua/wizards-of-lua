Vec3 = {}

function Vec3.from(x,y,z)
  return Vec3:new({x=x,y=y,z=z})
end

function Vec3:new(o)
  local o = o or {}
  o.x = o.x or 0
  o.y = o.y or 0
  o.z = o.z or 0
  setmetatable(o, self)
  self.__index = self
  return o
end

--function Vec3:add(v2)
--  local x = self.x+v2.x
--  local y = self.y+v2.y
--  local z = self.z+v2.z
--  return Vec3.from(x,y,z)
--end


