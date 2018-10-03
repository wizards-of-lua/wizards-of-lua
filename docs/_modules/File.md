---
name: File
subtitle: The File Module
type: module
layout: module
properties:
functions:  
  - name: close
    parameters:
    results: 'nil'
    description: |
        The <span class="notranslate">'close'</span> function closes this file.

        #### Example

        Writing some text into the file "my-file.txt".

        ```lua
        local f,err = io.open('my-file.txt','w')        
        if err then
          error(err)
        end
        f:write('some text')
        f:close()
        ```
  - name: flush
    parameters:
    results: 'nil'
    description: |
        The <span class="notranslate">'flush'</span> saves any written data to this file.

        #### Example

        Writing for ten seconds every second some text into the file "my-file.txt".

        ```lua
        local f,err = io.open('my-file.txt','w')     
        if err then
          error(err)
        end   
        for i=1,10 do
          f:write('line '..i)
          f:flush()
          sleep(20)
        end
        f:close()

        ```
  - name: lines
    parameters: format...
    results: any...
    description: |
        The <span class="notranslate">'lines'</span> function returns an iterator function that, each time it is called,
        reads the file according to the given formats. For a description of the supported formats please see the [File.read()](/modules/File/#read) function.

        When no format is given, uses "*l" as a default.

        In case of errors this function raises the error, instead of returning an error code.
  - name: read
    parameters: format...
    results: any
    description: |
        The <span class="notranslate">'read'</span> function reads this file, according to the given formats,
        which specify what to read.

        For example
        ```lua
        local line = file:read("*l")
        ```
        reads the next line of the current file.

        For each format, the function returns a string or a number with the characters read, or nil if it cannot read data with the specified format. (In this latter case, the function does not read subsequent formats.) When called without formats, it uses a default format that reads the next line (see below).

        The available formats are

        - "*n": reads a number. This is the only format that returns a number instead of a string.
        - "*a": reads the whole file, starting at the current position. On end of file, it returns the empty string.
        - "*l": (lower case L) reads the next line skipping the end of line, returning nil on end of file. This is the default format.
        - "*L": reads the next line keeping the end-of-line character (if present), returning nil on end of file.
        * number: reads a string with up to that number of bytes, returning nil on end of file. If number is zero, it reads nothing and returns an empty string, or nil on end of file.

        The formats "l" and "L" should be used only for text files.

        #### Example

        Reading the file "my-file.txt" line by line and printing it.

        ```lua
        local f,err = io.open('my-file.txt','r')
        if err then
          error(err)
        end  
        while true do
          local line = f:read('*l')
          if not line then
            break
          end
          print(line)
        end
        f:close()
        ```
  - name: seek
    parameters: 'whence, offset'
    results: 'number, string'
    description: |
        The <span class="notranslate">'seek'</span> function sets and gets the file position, measured from the beginning of the file,
        to the position given by offset plus a base specified by the string whence, as follows:

        - "set": base is position 0 (beginning of the file);
        - "cur": base is current position;
        - "end": base is end of file;

        In case of success, seek returns the final file position, measured in bytes from the beginning of the file. If seek fails, it returns nil, plus a string describing the error.

        The default value for whence is "cur", and for offset is 0. Therefore, the call ```file:seek()``` returns the current file position, without changing it; the call ```file:seek("set")``` sets the position to the beginning of the file (and returns 0); and the call ```file:seek("end")``` sets the position to the end of the file, and returns its size.

        #### Example

        Reading the last 100 bytes of the file "level.dat".

        ```lua
        local f,err = io.open('level.dat','r')
        f:seek('end',-100)
        local bytes = f:read("*a")
        f:close()
        ```
  - name: setvbuf
    unsupported: true
    parameters: 'mode, size'
    results: 'nil'
  - name: write
    parameters: any...
    results: "[File](/modules/File/)"
    description: |
        The <span class="notranslate">'write'</span> function writes the value of each of its arguments to this file.
        The arguments must be strings or numbers.

        In case of success, this function returns this [File](/modules/File/).
        Otherwise it returns nil plus the error message.

        #### Example

        Appending the current date and time, followed by the number of players to a file called "players.log".

        ```lua
        local f,err = io.open('players.log','a')
        if err then
          error(err)
        end
        local p = Entities.find('@a')
        _,err = f:write( Time.getDate(), '\n', #p, '\n')
        if err then
          error(err)
        end
        f:close()
        ```

        #### Example

        Writing the current player's inventory to a file called "inventory.txt".

        ```lua
        local player = spell.owner
        local f,err = io.open('inventory.txt','w')
        if err then
          error(err)
        end
        local data = {
          _player = player.name,
          inventory = player.nbt.Inventory
        };
        _,err = f:write(str(data))
        if err then
          error(err)
        end
        f:close()
        ```
---

The <span class="notranslate">File</span> module is a standard Lua function library that provides functions for operating on a regular file, like reading or writing stuff. References to files can be obtained by functions of the "[io](/modules/io/)" module.
