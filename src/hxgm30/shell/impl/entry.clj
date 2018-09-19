(ns hxgm30.shell.impl.entry
  (:require
    [clojure.java.io :as io]
    [hxgm30.common.util :as util]
    [hxgm30.shell.impl.base :as base]
    [hxgm30.shell.reader.parser :as reader]
    [taoensso.timbre :as log])
  (:import
    (java.net InetAddress)
    (java.util Date))
  (:refer-clojure :exclude [empty read print]))

(defrecord EntryShell [
  disconnect-handler
  disconnect-command
  options])

(defn prompt
  [this]
  "\r\nentry> ")

(defn banner
  [this]
  (str "\r\nWelcome to"
       (slurp (io/resource "text/banner.txt"))
       "\r\nRunning on "
       (.getHostName (InetAddress/getLocalHost))
       "\r\n"
       "Current server time: " (new Date) "\r\n"))

(defn motd
  [this]
  (str "You have connected to the top-level shell of a Hexagram 30 server. "
       "If you don't have a user account, can can regsiter to create one; "
       "if you do, you may create a new playing character or log in to the "
       "game world of your choice with a character you have already created."))

(defn connect-help
  [this]
  (str "To see a list of commands, type 'commands'; for help on any of the "
       "commands, type 'help <command name>'. To get help on a command's "
       "subcommand, 'help <command name> <subcommand name>', etc."))

(defn on-connect
  [this]
  (str (banner this)
       "\r\n"
       (util/wrap-paragraph (motd this) 76 4)
       (util/wrap-paragraph (connect-help this) 76 4)
       "\r\n"))

(defn read
  [this line]
  (log/debug "Reading command ...")
  (reader/parse (str "entry" \space line)))

(defn evaluate
  [this parsed]
  (log/debug "Evaluating command ...")
  parsed)

(defn print
  [this evaled]
  (log/debug "Printing evaluated result ...")
  evaled)

(defn handle-line
  [this line]
  (print
    this
    (evaluate
      this
      (read this line))))

(def behaviour
  {:banner banner
   :connect-help connect-help
   :disconnect? base/disconnect?
   :evaluate evaluate
   :handle-line handle-line
   :motd motd
   :print print
   :prompt prompt
   :on-connect on-connect
   :read read})

(defn create
  ([]
    (create {}))
  ([opts]
    (map->EntryShell {:options opts
                      :disconnect-command :quit})))
