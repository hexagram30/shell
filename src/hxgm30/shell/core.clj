(ns hxgm30.shell.core
  (:require
    [hxgm30.shell.impl.entry :as entry])
  (:import
    (clojure.lang Keyword)
    (hxgm30.shell.impl.entry EntryShell))
  (:refer-clojure :exclude [print read]))

(defprotocol ShellAPI
  (banner [this]
    "Return a string to be used as a banner. Optionally takes a data structure
    that implementations may use to customize presentation of banner.")
  (motd [this]
    "Return a string representing the message of the day. Usually this is
    presented as some form of greeting or introductory text after the banner.")
  (connect-help [this]
    "Return a string providing basic hints on usage of the top-level shell.")
  (on-connect [this]
    "The actions to take when the user connects. It is expected that shells
    will use this method to call `banner`, `motd`, and `connect-help`.")
  (read [this line]
    "Read and parse a line of input from the user.

    This function is not intended to be used alone, but rather as part of the
    classic read-evaluate-print chain; see handle-line.")
  (evaluate [this parsed]
    "Once parsed, this function will evaluate the command (using the functions
    defined in the shell's grammar).

    This function is not intended to be used alone, but rather as part of the
    classic read-evaluate-print chain; see handle-line.")
  (print [this evaled]
    "After a command has been read and evaluated, it's ready to print. While
    this method isn't responsible for actual printing (that's the domain of
    the component which manages the communication between client and server,
    e.g., a telnet connection), the name 'print' has been retained as a nod to
    the REPLs from which it is derrived.

    This function is not intended to be used alone, but rather as part of the
    classic read-evaluate-print chain; see handle-line.")
  (prompt [this]
    "Return the string to use as the shell prompt.")
  (disconnect? [this cmd]
    "Given a command, return true if it matches what's been stored in the
    `:disconnect-command` field. Note that `:disconnect-command` is passed
    as an option when creating a shell.")
  (handle-line [this line]
    "This is the function responsible for executing the read-evaluate-print
    chain, and it will continue to loop over new input lines until either
    the user switches to another shell or closes the connection to the server."))

(extend EntryShell
        ShellAPI
        entry/behaviour)

(defn create
  ([^Keyword shell-type]
    (create shell-type {}))
  ([^Keyword shell-type opts]
    (case shell-type
      :entry (entry/create opts)
      :unsupprted-shell)))
