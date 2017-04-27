class("Entity")

function Entity:hasTag( aTag)
  for _,tag in pairs(self.tags) do
    if tag == aTag then 
      return true
    end
  end
  return false
end