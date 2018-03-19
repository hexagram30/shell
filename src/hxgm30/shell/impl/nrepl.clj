(ns hxgm30.shell.impl.nrepl
  (:require
    [clojure.java.io :as io]
    [clojure.tools.nrepl :as repl]
    [taoensso.timbre :as log])
  (:refer-clojure :exclude [empty parse]))

(defrecord REPLShell [
  conn
  prompt])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;(defn ->request)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   ShellAPI   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn render
  [this response]
  (str
   response
   (or (:prompt this) "\n=> "))) ;; XXX promts need to be pulled from cfg

(defn banner
  [this]
  ;; XXX the file path for this should be pulled from configuration
  (slurp (io/resource "text/shell-login-banner.txt")))

(defn disconnect
  [this & args]
  (apply (:disconnect-handler this) args))

(defn parse
  [this request]
  (-> (repl/client (:conn this) 1000) ; timeout
      (repl/message {:op "eval" :code request})
      repl/response-values))


(defn handle-request
  [this request]
  (let [response (parse this request)
        _ (log/warn "Got response:" response)
        resp-msg {:response response
                  :message (render this response)}]
    resp-msg))

(defn handle-disconnect
  [this response future]
  (when (= (:disconnect-command this) (:command response))
    (disconnect this future)))

(def behaviour
  {:banner banner
   :parse parse
   :render render
   :handle-request handle-request
   :handle-disconnect handle-disconnect})

(defn create-shell
  ([]
    (create-shell {}))
  ([opts]
    (map->REPLShell (merge {:conn (repl/connect :port 1131)
                             :prompt "\n=> "}
                            opts))))
