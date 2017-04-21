Spell = class("Spell")

function Spell:select( sel)
  table.insert( sel, self.pos)
end
