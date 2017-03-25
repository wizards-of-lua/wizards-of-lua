Vec3d = class("Vec3d")

function check.Vec3d(c,i)
  local t=type(c)
  if tonumber(i) then
    assert(getmetatable(c)==Vec3d, "bad argument #%d (Vec3d expected, got %s)",i,t)
  elseif type(i) == "string" then
    assert(getmetatable(c)==complex, "bad argument '%s' (Vec3d expected, got %s)",i,t)
  else
    error("Illegal position argument for check call: %s",i)
  end
end

function Vec3d.from(x,y,z)
  check.number(x,1)
  check.number(y,2)
  check.number(z,3)
  return Vec3d:new({x=x,y=y,z=z})
end

function Vec3d:new(o)
  local o = o or {}
  o.x = o.x or 0
  o.y = o.y or 0
  o.z = o.z or 0
  check.number(o.x,"o.x")
  check.number(o.y,"o.y")
  check.number(o.z,"o.z")
  setmetatable(o, Vec3d)
  return o
end

function Vec3d:tostring()
  return "{" .. self.x .. ", " .. self.y .. ", " .. self.z .. "}"
end
Vec3d.__tostring = Vec3d.tostring

function Vec3d.add(v1,v2)
  check.Vec3d(v1,1)
  check.Vec3d(v2,2)
  local x = v1.x+v2.x
  local y = v1.y+v2.y
  local z = v1.z+v2.z
  return Vec3d.from(x,y,z)
end
Vec3d.__add = Vec3d.add

function Vec3d.substract(v1,v2)
  check.Vec3d(v1,1)
  check.Vec3d(v2,2)
  local x = v1.x-v2.x
  local y = v1.y-v2.y
  local z = v1.z-v2.z
  return Vec3d.from(x,y,z)
end
Vec3d.__sub = Vec3d.substract

function Vec3d:sqrMagnitude()
  return v1.x*v1.x + v1.y*v1.y + v1.z*v1.z
end

function Vec3d:magnitude()
  return math.sqrt(self:sqrMagnitude())
end

function Vec3d.dotProduct(v1,v2)
  check.Vec3d(v1,1)
  check.Vec3d(v2,2)
  return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z
end

function Vec3d.scale(a,b)
  local v,f
  if tonumber(a) then
    check.Vec3d(b,2)
    v,f=b,a
  elseif type(a) == "Vec3d" then
    check.number(b,2)
    v,f=a,b
  else
    error("bad argument #%d (number or Vec3d expected, got %s)",1,type(a))
  end
  return Vec3d.from(v.x*f, v.y*f, v.z*f)
end
Vec3d.__mul = function(a,b)
  if tonumber(a) or tonumber(b) then
    return Vec3d.scale(a,b)
  else
    return Vec3d.dotProduct(a,b)
  end
end

function Vec3d.invert(v1)
  check.Vec3d(v1,1)
  return Vec3d.from(-v1.x, -v1.y, -v1.z)
end
Vec3d.__unm = Vec3d.invert

function Vec3d.__concat(a,b)
  return tostring(a)..tostring(b)
end
