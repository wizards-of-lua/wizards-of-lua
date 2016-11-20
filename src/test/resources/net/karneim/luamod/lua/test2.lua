
function add(x,y)
  return x+y
end

tbl = {}
for i=1,10 do
  table.insert(tbl, add(i,i+1))
end

for i,v in ipairs(tbl) do
  print(i,v)
end
