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
  [this line]
  (log/debug "Reading command ...")
  (parser/parse (:parser this) (:grammar this) line))

(defn print
  ([this evaled]
    (log/debug "Printing evaluated result ...")
    evaled)
  ([this {cmd :cmd} evaled]
    (log/debug "Printing evaluated result ...")
    evaled))

(defn connect-help
  [this]
  (str "To see a list of commands, type 'commands' or 'help'. Documentation is "
       "is also available for indiviual commands and/or their subcommands, "
       "using `help` in the following manner:"
       common-grammar/help-usage))
