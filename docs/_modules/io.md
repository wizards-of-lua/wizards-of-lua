---
name: IO
title: io
subtitle: Doing File Operations
type: module
layout: module
properties:
functions:  
  - name: close
    parameters: file
    results: 'boolean'
    description: |
        The <span class="notranslate">'close'</span> function is equivalent to [File.close()](/modules/File/#close).
        Without a specified file it closes the default output file.

        This function returns true if the file has been closed.

        #### Example

        Writing some text into the file "my-file.txt".

        ```lua
        io.output('my-file.txt')
        io.write('some text')
        io.close()
        ```
  - name: flush
    parameters:
    results: 'boolean'
    description: |
        The <span class="notranslate">'flush'</span> function is equivalent to ```io.output():flush()```.
        Saves any written data to the default output file.

        This function returns true if the file has been flushed.

        #### Example

        Writing for ten seconds every second some text into the file "my-file.txt".

        ```lua
        io.output('my-file.txt')
        for i=1,10 do
          io.write('line '..i)
          io.flush()
          sleep(20)
        end
        io.close()

        ```
  - name: input
    parameters: file
    results: "[File](/modules/File/)"
    description: |
        The <span class="notranslate">'input'</span> function defines the default input file. It returns the file that is
        currently defined as the default input file.

        When called with a file name, it opens the named file (in text mode), and sets its handle as the default input file.
        When called with a file handle, it simply sets this file handle as the default input file.
        When called without arguments, it just returns the current default input file.

        #### Example

        Setting "my-file.txt" to be the default input file.

        ```lua
        io.input('my-file.txt')
        ```
  - name: lines
    unsupported: true
    parameters: filename, formats...
    results: iterator  
  - name: open
    parameters: filename, mode
    results: "[File](/modules/File/), string"
    description: |
        The <span class="notranslate">'open'</span> functions opens the file with the given filename in the specified mode.
        In case of success, it returns a new file handle. If it fails it returns nil and the error message.

        The mode string can be any of the following:

        - "r": read mode (the default)
        - "w": write mode
        - "a": append mode
        - "r+": update mode, all previous data is preserved. <span style="color:#ff6666">*Currently not supported!*</span>
        - "w+": update mode, all previous data is erased. <span style="color:#ff6666">*Currently not supported!*</span>
        - "a+": append update mode, previous data is preserved, writing is only allowed at the end of file. <span style="color:#ff6666">*Currently not supported!*</span>

        The mode string can also have a 'b' at the end, which is needed in some systems to open the file in binary mode.

        #### Example

        Opening the file "my-file.txt" and reading all of its contents.

        ```lua
        local f,err = io.open('my-file.txt','w')
        if err then
          error(err)
        end   
        local contents = f:real("*a")
        f:close()
        ```
  - name: output
    parameters: file
    results: "[File](/modules/File/)"
    description: |
        The <span class="notranslate">'output'</span> function defines the default output file. It returns the file that is
        currently defined as the default output file.

        When called with a file name, it opens the named file (in text mode), and sets its handle as the default output file.
        When called with a file handle, it simply sets this file handle as the default output file.
        When called without arguments, it just returns the current default output file.

        #### Example

        Setting "my-file.txt" to be the default output file.

        ```lua
        io.output('my-file.txt')
        ```    
  - name: popen
    unsupported: true
    parameters: prog, mode
    results: "[File](/modules/File/)"      
  - name: read
    parameters: format...
    results: any
    description: |
        The <span class="notranslate">'read'</span> function is equivalent to ```io.input():read(...)```.
        See [File.read()](/modules/File/#read).

        #### Example

        First setting "my-file.txt" to be the default input file. Then reading and printing it line by line.

        ```lua
        io.input('my-file.txt')
        while true do
          local line = io.read("*l")
          if not line then
            break
          end
          print(line)
        end
        io.close()
        ```
  - name: tmpfile
    unsupported: true
    parameters:
    results: "[File](/modules/File/)"    
  - name: type
    parameters: any
    results: boolean
    description: |
        The <span class="notranslate">'type'</span> function checks whether the given object is a valid [File](/modules/File/) handle.
        Returns the string "file" if obj is an open file handle, "closed file" if obj is a closed file handle, or nil if obj is not a file handle.

        #### Example

        Checking the file handle of the player's advancements file before and after the file has been closed.

        ```lua
        local name ="advancements/"..spell.owner.uuid..".json"
        local f = io.open(n,"r")
        print(io.type(f))
        f:close()
        print(io.type(f))
        ```     
  - name: write
    parameters: any...
    results: file, string
    description: |
        The <span class="notranslate">'write'</span> function is equivalent to [io.output:write()](/modules/File/#write).
---

The <span class="notranslate">io</span> module (with lowercase letters I and O) is a standard Lua function library that provides functions for reading and writing files.

Please note that WoL interprets any path passed to the io functions as to be relative to the server's world folder.
To browse the server's world folder please use the function "[System.listFiles()](/modules/System/#listFiles)".
