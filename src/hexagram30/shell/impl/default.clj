(ns hexagram30.shell.impl.default
  (:require
    [hexagram30.shell.parser :as parser]
    [taoensso.timbre :as log])
  (:refer-clojure :exclude [empty parse]))

(defrecord DefaultShell [
  disconnect-command
  disconnect-handler])

(defn banner
  [this]
  )

(defn disconnect
  [this & args]
  (apply (:disconnect-handler this) args))

(defn empty
  [this request]
  )

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

(defn handle-exception
  [this request]
  )

(defn handle-disconnect
  [this response future]
  (when (= (:disconnect-command this) (:command response))
    (disconnect this future)))

(def behaviour
  {:banner banner
   :disconnect disconnect
   :empty empty
   :parse parse
   :render render
   :handle-request handle-request
   :handle-exception handle-exception
   :handle-disconnect handle-disconnect})
