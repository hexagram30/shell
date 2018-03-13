(ns hxgm30.shell.impl.default
  (:require
    [hxgm30.shell.parser :as parser]
    [taoensso.timbre :as log])
  (:refer-clojure :exclude [empty parse]))

(defrecord DefaultShell [
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
  (apply format (concat [(:result-tmpl response)]
                        (:result-args response))))

(defn handle-request
  [this request]
  (let [response (parse this request)]
    {:response response
     :message (render this response)}))

(defn handle-disconnect
  [this response future]
  (when (= (:disconnect-command this) (:command response))
    (disconnect this future)))

(def behaviour
  {:banner banner
   :disconnect disconnect
   :parse parse
   :render render
   :handle-request handle-request
   :handle-disconnect handle-disconnect})
