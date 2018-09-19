(ns hxgm30.shell.impl.base
  (:require
    [hxgm30.shell.reader.parser :as parser]
    [taoensso.timbre :as log])
  (:refer-clojure :exclude [empty parse]))

(defrecord DefaultShell [
  disconnect-command])

(defn banner
  [this]
  :not-implemented)

(defn disconnect?
  [this cmd]
  (= (:disconnect-command this) cmd))

(defn parse
  [this request]
  (parser/parse (:disconnect-command this) request))

(def behaviour
  {:banner banner
   :disconnect? disconnect?
   :parse parse})
