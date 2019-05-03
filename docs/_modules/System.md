---
name: System
title: System
subtitle: Interacting with the Server's OS
type: module
layout: module
properties:
functions:
  - name: 'delete'
    parameters: path
    results: 'boolean'
    description: |
        The <span class="notranslate">'delete'</span> function deletes the file with the given path.
        The path is interpreted relative to the server's world folder. This function returns true if
        the file did exist and has been deleted.
        
        Please note that deleting a directory is only supported if its empty.
        
        #### Example
        
        Deleting the file "some-file-to-delete.txt" from the server's world folder.
        
        ```lua 
        System.delete('/some-file-to-delete.txt')
        ```
  - name: 'execute'
    parameters: name, arg...
    results: 'number, string'
    description: |
        The <span class="notranslate">'execute'</span> function invokes the program with the given name
        and the given arguments on the server's operating system. Please note that you only can execute
        programs living inside the server's [script gateway directory](/configuration-file). Please
        note that this is a blocking call. The spell will resume its execution only after the program
        has terminated.
        
        #### Example
        
        Calling the "echo.sh" shell script from the server's script gateway directory.
        
        ```lua 
        exitcode, result = System.execute('echo.sh','some argument')
        print('exitcode', exitcode)
        print('result', result)
        ```
  - name: 'isDir'
    parameters: path
    results: 'boolean'
    description: |
        The <span class="notranslate">'isDir'</span> function checks whether the given path points to a
        directory (in contrast to a regular file). The path is interpreted relative to the server's
        world folder.
        
        #### Example
        
        Printing the file type of the file "some/file" inside the server's world folder.
        
        ```lua
        local path = '/some/file'
        if System.isFile(path) then
          print(string.format('% is a regular file',path))
        end
        if System.isDir(path) then
          print(string.format('% is a directory',path))
        end
        ```
  - name: 'isFile'
    parameters: path
    results: 'boolean'
    description: |
        The <span class="notranslate">'isFile'</span> function checks whether the given path points to
        a regular file (in contrast to a directory). The path is interpreted relative to the server's
        world folder.
        
        #### Example
        
        Printing the file type of the file "some/file" inside the server's world folder.
        
        ```lua
        local path = '/some/file'
        if System.isFile(path) then
          print(string.format('% is a regular file',path))
        end
        if System.isDir(path) then
          print(string.format('% is a directory',path))
        end
        ```
  - name: 'listFiles'
    parameters: path
    results: 'table'
    description: |
        The <span class="notranslate">'listFiles'</span> function returns a table with the names of all
        files that exist inside the directory at the given path. The path is interpreted relative to
        the server's world folder.
        
        #### Example
        
        Printing the names of all files inside the "region" folder of the server's world folder.
        
        ```lua
        local path = '/region'
        local names = System.listFiles(path)
        for _,name in pairs(names) do
          print(name)
        end
        ```
        
        #### Example
       
        Getting the names of all files inside server's world folder.
       
        ```lua        
        local names = System.listFiles('/')
        ```
  - name: 'makeDir'
    parameters: path
    results: 'boolean'
    description: |
        The <span class="notranslate">'makeDir'</span> function creates a new directory with the given
        path if it did not already exist. The path is interpreted relative to the server's world
        folder. This function returns true if the directory already existed or if it has been be
        created.
        
        #### Example
        
        Creating the directory "some/dir" in the server's world folder.
        
        ```lua
        local created = System.makeDir('/some/dir')
        if not created then
          error('Could not create directory')
        end
        ```
  - name: 'move'
    parameters: path, newPath
    results: 'boolean'
    description: |
        The <span class="notranslate">'move'</span> function moves or renames the file with the given
        path so that the resulting file is accessible by the given new path. The path is interpreted
        relative to the server's world folder. This function returns true if the operation was
        successful.
        
        #### Example
        
        Renaming the file "aaa.txt" to "bbb.txt"
        
        ```lua 
        System.move('aaa.txt','bbb.txt')
        ```
---

The <span class="notranslate">System</span> module provides functions for interacting with the
server's operating system.
