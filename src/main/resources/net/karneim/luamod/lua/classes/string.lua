-- Additional String Functions 

function string.starts(str,start)
   return string.sub(str,1,string.len(start))==start
end

function string.ends(str,endstr)
   return endstr=='' or string.sub(str,-string.len(endstr))==endstr
end

