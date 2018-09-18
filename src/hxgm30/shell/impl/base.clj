(ns hxgm30.shell.impl.base
  (:require
    [hxgm30.shell.reader.parser :as parser]
    [taoensso.timbre :as log])
  (:refer-clojure :exclude [empty parse]))

(defrecord DefaultShell [
  grammar-type
  prompt
  legal-subshells
  active-subshell
  disconnect-command
  disconnect-handler])

(defn banner
  [this]
  :not-implemented)

(defn disconnect
  [this & args]
  (apply (:disconnect-handler this) args))

(defn parse
  [this request]
  (parser/parse (:disconnect-command this) request))

(defn render
  [this response]
  (str
    (apply format (concat [(:result-tmpl response)]
                          (:result-args response)))
    (or (:prompt this) "")))

(defn handle-request
  [this request]
  (let [response (parse this request)]
    {:response response
     :message (render this response)}))

(defn handle-disconnect
  [this response future]
  (if (= (:disconnect-command this) (:command response))
    (disconnect this future)
    (log/debug "Disconnect command not passed; continuing ...")))

(def behaviour
  {:banner banner
   :disconnect disconnect
   :parse parse
   :render render
   :handle-request handle-request
   :handle-disconnect handle-disconnect})
