
function showPkg(pkg)
  local tbl = package[pkg]
  if tbl == nil then
    print(pkg..": <does not exist>")
  elseif type(tbl)=="table" then
    print(pkg..": <Table>")
    showTable(tbl)
  elseif type(tbl)=="function" then
    print(pkg..": <function>")
  else
    print(pkg..": "..tbl)
  end
  print()
end

function showTable(tbl)
  for k,v in pairs(tbl) do
    print("  "..k,v)
  end
end

function myload(modname)
  print("myload",modname)
  local loader = function(p)
    print("loading ",p)
    local mod = {}
    mod.debug = function()
      print("yes") 
    end
    package.loaded._G["xxx"] = mod
    return mod
  end
  local par = modname
  return loader,par
end

table.insert(package.searchers,myload)
package.path = "/Users/karneim/devel/minecraft/ycommands-mc-1.10.2/?.lua" 

showPkg("loaded")
showPkg("searchers")
showPkg("preload")
showPkg("path")
showPkg("cpath")
showPkg("searchpath")



require "dummy"

