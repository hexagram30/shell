(ns hxgm30.shell.impl.base
  (:require
    [hxgm30.shell.reader.grammar.common :as common-grammar]
    [hxgm30.shell.reader.parser.base :as parser]
    [taoensso.timbre :as log])
  (:refer-clojure :exclude [empty parse print read]))

(defrecord DefaultShell [
  disconnect-command])

(def commands-with-list-output #{:commands})

(defn banner
  [this]
  :not-implemented)

(defn disconnect?
  [this cmd]
  (= (:disconnect-command this) cmd))

(defn parse
  [this request]
  (parser/parse (:parser this) (:grammar this) request))

(def behaviour
  {:banner banner
   :disconnect? disconnect?
   :parse parse})

(def default-options {:disconnect-command :quit})

(defn read
  ([this line]
    (read this nil line))
  ([this session-id line]
    (log/debugf "Reading command (session-id: %s)..." session-id)
    (parser/parse (:parser this) (:grammar this) line)))

(defn print
  ([this evaled]
    (print this {} evaled))
  ([this cmd-data evaled]
    (print this nil cmd-data evaled))
  ([this session-id {cmd :cmd} evaled]
    (log/debugf "Printing evaluated result (session-id: %s)..." session-id)
    evaled))

(defn connect-help
  [this]
  (str "To see a list of commands, type 'commands' or 'help'. Documentation is "
       "is also available for indiviual commands and/or their subcommands, "
       "using `help` in the following manner:"
       common-grammar/help-usage))
