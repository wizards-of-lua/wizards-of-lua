---
name: Logger
title: Logger
subtitle: Logging Messages
type: class
extends: Object
layout: module
properties:
functions:
  - name: 'debug'
    parameters: message, arg...
    results: '[nil](/modules/nil)'
    description: |
        The <span class="notranslate">'debug'</span> function writes the given debug message into the
        server's log file if the log level is at least 'debug', prefixed with this logger's name.
       
        Optionally you can provide some message arguments that will be formatted into the final
        message ([see
        string.format()](https://www.lua.org/manual/5.3/manual.html#pdf-string.format)).
       
        #### Example
       
        Printing a debug message into the server's log file, prefixed with the category label
        "my-logger".
       
        ```lua
        local logger = Loggers.get("my-logger")
        logger:debug("Some debug message")
        ```
       
        #### Example
       
        Printing a formatted debug message into the server's log file, prefixed with the category
        label "my-logger.
       
        ```lua
        local logger = Loggers.get("my-logger")
        logger:debug("Some debug message with some value %s", value)
        ```
  - name: 'error'
    parameters: message, arg...
    results: '[nil](/modules/nil)'
    description: |
        The <span class="notranslate">'error'</span> function writes the given error message into the
        server's log file if the log level is at least 'error', prefixed with this logger's name.
       
        Optionally you can provide some message arguments that will be formatted into the final
        message ([see
        string.format()](https://www.lua.org/manual/5.3/manual.html#pdf-string.format)).
       
        #### Example
       
        Printing an error message into the server's log file, prefixed with the category label
        "my-logger".
       
        ```lua
        local logger = Loggers.get("my-logger")
        logger:error("Some error message")
        ```
       
        #### Example
       
        Printing a formatted error message into the server's log file, prefixed with the category
        label "my-logger.
       
        ```lua
        local logger = Loggers.get("my-logger")
        logger:error("Some error message with some value %s", value)
        ```
  - name: 'info'
    parameters: message, arg...
    results: '[nil](/modules/nil)'
    description: |
        The <span class="notranslate">'info'</span> function writes the given information message
        into the server's log file if the log level is at least 'info', prefixed with this logger's
        name.
       
        Optionally you can provide some message arguments that will be formatted into the final
        message ([see
        string.format()](https://www.lua.org/manual/5.3/manual.html#pdf-string.format)).
       
        #### Example
       
        Printing an info message into the server's log file, prefixed with the category label
        "my-logger".
       
        ```lua
        local logger = Loggers.get("my-logger")
        logger:info("Some info message")
        ```
       
        #### Example
       
        Printing a formatted info message into the server's log file, prefixed with the category
        label "my-logger.
       
        ```lua
        local logger = Loggers.get("my-logger")
        logger:info("Some info message with some value %s", value)
        ```
  - name: 'trace'
    parameters: message, arg...
    results: '[nil](/modules/nil)'
    description: |
        The <span class="notranslate">'trace'</span> function writes the given tracing message into
        the server's log file if the log level is at least 'trace', prefixed with this logger's name.
       
        Optionally you can provide some message arguments that will be formatted into the final
        message ([see
        string.format()](https://www.lua.org/manual/5.3/manual.html#pdf-string.format)).
       
        #### Example
       
        Printing a tracing message into the server's log file, prefixed with the category label
        "my-logger".
       
        ```lua
        local logger = Loggers.get("my-logger")
        logger:trace("Some tracing message")
        ```
       
        #### Example
       
        Printing a formatted traceing message into the server's log file, prefixed with the category
        label "my-logger.
       
        ```lua
        local logger = Loggers.get("my-logger")
        logger:trace("Some traceing message with some value %s", value)
        ```
  - name: 'warn'
    parameters: message, arg...
    results: '[nil](/modules/nil)'
    description: |
        The <span class="notranslate">'warn'</span> function writes the given warning message into
        the server's log file if the log level is at least 'warn', prefixed with this logger's name.
       
        Optionally you can provide some message arguments that will be formatted into the final
        message ([see
        string.format()](https://www.lua.org/manual/5.3/manual.html#pdf-string.format)).
       
        #### Example
       
        Printing a warning message into the server's log file, prefixed with the category label
        "my-logger".
       
        ```lua
        local logger = Loggers.get("my-logger")
        logger:warn("Some warning message")
        ```
       
        #### Example
       
        Printing a formatted warning message into the server's log file, prefixed with the category
        label "my-logger.
       
        ```lua
        local logger = Loggers.get("my-logger")
        logger:warn("Some warning message with some value %s", value)
        ```
---

The <span class="notranslate">Logger</span> class supports writing log messages into the server's
log file.

The log messages can be of the following severity: error, warn, info, debug, and trace.

The server's log files are found inside the server's ```logs``` folder. By default "error",
"warn", and "info" messages go into the files "latest.log" and "debug.log", while "debug" and
"trace" messages only go into "debug.log".
