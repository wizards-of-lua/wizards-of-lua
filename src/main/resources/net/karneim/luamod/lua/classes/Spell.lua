Spell = class("Spell")

function Spell:select( sel)
  table.insert( sel, self.pos)
end

local execute_ = Spell.execute

function Spell:execute( cmd, ...)
  local cmd = string.format( cmd, ...)
  return execute_( self, cmd)
end
