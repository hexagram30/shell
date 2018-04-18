(ns hxgm30.shell.core
  (:require
    [hxgm30.shell.impl.base :as base]
    [hxgm30.shell.impl.demo :as demo]
    [hxgm30.shell.impl.login :as login])
  (:import
    (hxgm30.shell.impl.login LoginShell)
    (hxgm30.shell.impl.demo DemoShell))
  (:refer-clojure :exclude [empty parse]))

(defprotocol ShellAPI
  (banner [this] [this data]
    "Return a string to be used as a banner. Optionally takes a data structure
    that implementations may use to customize presentation of banner.")
  (create-subshell! [this subshell-type]
    "Create a new subshell that will be used to parse commands until the user
    is disconnected from the subshell.")
  (disconnect-subshell! [subshell-type]
    "Disconnect the user from the active subshell.")
  (disconnect [this & args]
    "Return string data for use in a final message to the client. Should be
    rendered just prior to actual disconnection.

    Note that there is also a disconnect function (possibly with the same name)
    defined in the Telnet application (handler namespace) and that these are
    not the same. They are related, though: the shell `defrecord`s that are
    used in the shell implementations define a `disconnect-handler` field that
    is to pass a function from the Telnet application down into the shell
    itself. The disconnect function here then uses the function stored in that
    field, calling it with `args`. This level of indrection is needed to work
    around the manner in which the Java netty.io API builds Telnet
    applications.")
  (get-prompt [this]
    "Return the shell prompt.")
  (parse [this request]
    "Parse a request message for further processing.

    The intended use of this function is to use an instantiation of a command
    parser to process the input from the user.

    While terminal implementations may choose to call this function directly,
    it is recommended that such implementations use the `handle-request`
    function instead. As such, shell implementations of the `handle-request`
    function should call `parse`.")
  (render [this reponse]
    "Given the result of a parse, render it as a string suitable to be consumed
    by the client.")
  (handle-request [this request]
    "Perform full procesing on a request message.

    This is the recommended function for handling request data in terminals.
    Additionally, implementations of this function should make calls to
    `disconnect`, `empty`, and `parse` as needed, assuring portability.")
  (handle-disconnect [this response data]
    "Perform the defined action (stored in the `:disconnect-handler` field)
    for a disconnect request.

    Use this method to check for a disconnect command and, if present,
    dispatch the disconnect function. This is intended for use by
    implementations that don't have access to the connection-ending code where
    messages are handled (such as Netty telnet applications).

    The `response` argument is the result of making a call to `handle-request`;
    as such, implementations of 'handle-request' may wish to call
    `handle-disconnect` as well, before returning the response.

    In the case of Netty telnet terminals, the `data` arguement
    is a `future`; in the case of mock terminals, as that provided by
    `hxgm30.terminal.util.networkless`, `data` is a `Handler` instance."))

(extend LoginShell
        ShellAPI
        (merge base/behaviour
               login/behaviour))

(extend DemoShell
        ShellAPI
        (merge base/behaviour
               demo/behaviour))

(def shell-fns
  {:login login/create-shell
   :demo demo/create-shell})

(def legal-subshells
  {:login [:demo]
   :demo []})

(defn get-legal-subshells
  [shell-type]
  (select-keys shell-fns (shell-type legal-subshells)))

(defn create-shell
  ([]
    (create-shell {}))
  ([opts]
    (create-shell :login opts))
  ([shell-type opts]
    (let [shell-fn (shell-type shell-fns)]
      (shell-fn (merge {:legal-subshells (get-legal-subshells shell-type)
                        :active-subshell (atom nil)}
                       opts)))))
