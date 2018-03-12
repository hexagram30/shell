(ns hxgm30.shell.core
  (:require
    [hxgm30.shell.impl.default :as default]
    [hxgm30.shell.impl.demo :as demo])
  (:import
    (hxgm30.shell.impl.default DefaultShell)
    (hxgm30.shell.impl.demo DemoShell))
  (:refer-clojure :exclude [empty parse]))

(defprotocol ShellAPI
  (banner [this] [this data]
    "Return a string to be used as a banner. Optionally takes a data structure
    that implementations may use to customize presentation of banner.")
  (disconnect [this & args]
    "Return string data for use in a final message to the client. Should be
    rendered just prior to actual disconnection.

    While terminal implementations may choose to call this function directly,
    it is recommended instead that such implementations use the
    `handle-request` function instead,")
  (empty [this request]
    "In the event that request data is empty, implementations may want to
    perform a specific action.

    While terminal implementations may choose to call this function directly,
    it is recommended instead that such implementations use the
    `handle-request` function instead,")
  (parse [this request]
    "The intended use of this function is to use an instantiation of a command
    parser to process the input from the user.

    While terminal implementations may choose to call this function directly,
    it is recommended instead that such implementations use the
    `handle-request` function instead, this function be used by")
  (render [this reponse]
    "Given the result of a parse, render it as a string suitable to be consumed
    by the client.")
  (handle-request [this request]
    "This is the recommended function for handling request data in terminals.
    Additionally, implementations of this function should make calls to
    `disconnect`, `empty`, and `parse` as needed, assuring flexibility of
    usage.")
  (handle-exception [this cause]
    "This function is responsible for displaying messages and taking other
    action when a terminal catches an exception.")
  (handle-disconnect [this context request]
    ""))

(extend DefaultShell
        ShellAPI
        default/behaviour)

(extend DemoShell
        ShellAPI
        (merge default/behaviour
               demo/behaviour))

(defn create-shell
  ([]
    (create-shell {}))
  ([opts]
    (create-shell :default opts))
  ([shell-type opts]
    (case shell-type
      :default (default/map->DefaultShell (merge {:disconnect-command "QUIT"}
                                                 opts))
      :demo (demo/map->DemoShell (merge {:disconnect-command "bye"}
                                        opts)))))
