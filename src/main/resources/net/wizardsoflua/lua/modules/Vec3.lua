-- Lua Module for the Vec3 class

declare("Vec3")

-- Checks if obj is an instance of Vec3. If not, a error is thrown.
-- TODO provide a generic function in check that can do this.
function Check.isVec3(obj,i)
  local ok=instanceOf(Vec3,obj)
  if i==nil then
    assert(ok, "bad argument (Vec3 expected, got %s)", type(obj))
  elseif tonumber(i) then
    assert(ok, "bad argument #%d (Vec3 expected, got %s)", i, type(obj))
  elseif type(i)=="string" then
    assert(ok, "bad argument '%s' (Vec3 expected, got %s)", i, type(obj))
  else
    error("Illegal position argument for check call: %s", i)
  end
end

function Vec3.from(x,y,z)
  Check.isNumber(x,1)
  Check.isNumber(y,2)
  Check.isNumber(z,3)
  return Vec3.new({x=x,y=y,z=z})
end

function Vec3.new(o)
  o = o or {}
  o.x = o.x or 0
  o.y = o.y or 0
  o.z = o.z or 0
  Check.isNumber(o.x,"o.x")
  Check.isNumber(o.y,"o.y")
  Check.isNumber(o.z,"o.z")
  setmetatable(o, Vec3)
  return o
end

function Vec3:tostring()
  return "{" .. self.x .. ", " .. self.y .. ", " .. self.z .. "}"
end
Vec3.__tostring = Vec3.tostring

function Vec3.add(v1,v2)
  Check.isVec3(v1,1)
  Check.isVec3(v2,2)
  local x = v1.x+v2.x
  local y = v1.y+v2.y
  local z = v1.z+v2.z
  return Vec3.from(x,y,z)
end
Vec3.__add = Vec3.add

function Vec3.substract(v1,v2)
  Check.isVec3(v1,1)
  Check.isVec3(v2,2)
  local x = v1.x-v2.x
  local y = v1.y-v2.y
  local z = v1.z-v2.z
  return Vec3.from(x,y,z)
end
Vec3.__sub = Vec3.substract

function Vec3:sqrMagnitude()
  return self.x*self.x + self.y*self.y + self.z*self.z
end

function Vec3:magnitude()
  return math.sqrt(self:sqrMagnitude())
end

function Vec3.dotProduct(v1,v2)
  Check.isVec3(v1,1)
  Check.isVec3(v2,2)
  return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z
end

function Vec3.scale(a,b)
  local v,f
  if tonumber(a) then
    Check.isVec3(b,2)
    v,f=b,a
  elseif instanceOf(Vec3,a) then
    Check.isNumber(b,2)
    v,f=a,b
  else
    error("bad argument #%d (number or Vec3 expected, got %s)",1,type(a))
  end
  return Vec3.from(v.x*f, v.y*f, v.z*f)
end
Vec3.__mul = function(a,b)
  if tonumber(a) or tonumber(b) then
    return Vec3.scale(a,b)
  else
    return Vec3.dotProduct(a,b)
  end
end

function Vec3.invert(v1)
  Check.isVec3(v1,1)
  return Vec3.from(-v1.x, -v1.y, -v1.z)
end
Vec3.__unm = Vec3.invert

function Vec3.__concat(a,b)
  return tostring(a)..tostring(b)
end

-- Here is some example code of how you could create a subclass of Vec3
--[[
class("Vec3n",Vec3)

function Vec3n.new(o)
  o = Vec3.new(o)
  o.n = o.n or "noname"
  Check.isString(o.n,"o.n")
  setmetatable(o, Vec3n)
  return o
end
--]]
