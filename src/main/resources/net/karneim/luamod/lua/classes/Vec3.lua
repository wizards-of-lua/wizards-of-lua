Vec3 = class("Vec3")

function check.Vec3(c,i)
  local t=type(c)
  if tonumber(i) then
    assert(getmetatable(c)==Vec3, "bad argument #%d (Vec3 expected, got %s)",i,t)
  elseif type(i) == "string" then
    assert(getmetatable(c)==Vec3, "bad argument '%s' (Vec3 expected, got %s)",i,t)
  else
    error("Illegal position argument for check call: %s",i)
  end
end

function Vec3.from(x,y,z)
  check.number(x,1)
  check.number(y,2)
  check.number(z,3)
  return Vec3:new({x=x,y=y,z=z})
end

function Vec3:new(o)
  local o = o or {}
  o.x = o.x or 0
  o.y = o.y or 0
  o.z = o.z or 0
  check.number(o.x,"o.x")
  check.number(o.y,"o.y")
  check.number(o.z,"o.z")
  setmetatable(o, Vec3)
  return o
end

function Vec3:tostring()
  return "{" .. self.x .. ", " .. self.y .. ", " .. self.z .. "}"
end
Vec3.__tostring = Vec3.tostring

function Vec3.add(v1,v2)
  check.Vec3(v1,1)
  check.Vec3(v2,2)
  local x = v1.x+v2.x
  local y = v1.y+v2.y
  local z = v1.z+v2.z
  return Vec3.from(x,y,z)
end
Vec3.__add = Vec3.add

function Vec3.substract(v1,v2)
  check.Vec3(v1,1)
  check.Vec3(v2,2)
  local x = v1.x-v2.x
  local y = v1.y-v2.y
  local z = v1.z-v2.z
  return Vec3.from(x,y,z)
end
Vec3.__sub = Vec3.substract

function Vec3:sqrMagnitude()
  return v1.x*v1.x + v1.y*v1.y + v1.z*v1.z
end

function Vec3:magnitude()
  return math.sqrt(self:sqrMagnitude())
end

function Vec3.dotProduct(v1,v2)
  check.Vec3(v1,1)
  check.Vec3(v2,2)
  return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z
end

function Vec3.scale(a,b)
  local v,f
  if tonumber(a) then
    check.Vec3(b,2)
    v,f=b,a
  elseif type(a) == "Vec3" then
    check.number(b,2)
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
  check.Vec3(v1,1)
  return Vec3.from(-v1.x, -v1.y, -v1.z)
end
Vec3.__unm = Vec3.invert

function Vec3.__concat(a,b)
  return tostring(a)..tostring(b)
end
