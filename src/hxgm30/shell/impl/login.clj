(ns hxgm30.shell.impl.login
  (:require
    [clojure.java.io :as io]
    [hxgm30.shell.grammar :as grammar]
    [hxgm30.shell.impl.base :as base]
    [hxgm30.shell.impl.demo :as demo]
    [hxgm30.shell.impl.subshell :as subshell]
    [hxgm30.shell.parser :as parser]
    [taoensso.timbre :as log])
  (:refer-clojure :exclude [empty parse]))

(defrecord LoginShell [
  grammar-type
  prompt
  legal-subshells
  active-subshell
  disconnect-command
  disconnect-handler])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   ShellAPI   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn render
  [this response]
  (let [result-tmpl (or (:result-tmpl response) "")
        result-args (remove nil? (:result-args response))
        args (vec (concat [result-tmpl] result-args))]
    (str
     (apply format args)
     ;; XXX prompt rendering should probably have its own function
     (if (subshell/active? this)
       (or (subshell/prompt this) "")
       (or (:prompt this) "\n> "))))) ;; XXX promts need to be pulled from cfg

(defn create-subshell!
  ([this subshell-type]
    (create-subshell! this
                      subshell-type
                      (parser/map->Response {:result-tmpl ""})))
  ([this subshell-type resp-msg]
    (let [subshell-opts {:disconnect-command "logout"
                         :prompt "\nsubshell> " ;; XXX promts need to be pulled from cfg
                         :type subshell-type}]
      (subshell/set! this (subshell/create this subshell-opts)))
    (when-not (nil? resp-msg)
      (log/debug "resp-msg:" resp-msg)
      (render this (:response resp-msg)))
    resp-msg))

(defn disconnect-subshell!
  [this resp-msg]
  (log/debug "Disconnecting from subshell ...")
  (subshell/set! this nil)
  resp-msg)

(defn banner
  [this]
  ;; XXX the file path for this should be pulled from configuration
  (slurp (io/resource "text/shell-login-banner.txt")))

(defn disconnect
  [this & args]
  (if (subshell/active? this)
    (disconnect-subshell! this)
    (apply (:disconnect-handler this) args)))

(defn parse
  [this request]
  (if (subshell/active? this)
    (parser/parse (subshell/type this)
                  (:disconnect-command (subshell/instance this))
                  request)
    (parser/parse :login
                  (:disconnect-command this)
                  request)))

(defn handle-request
  [this request]
  (let [response (parse this request)
        _ (log/warn "Got response:" response)
        resp-msg {:response response
                  :message (render this response)}]
    (cond (and (subshell/not-active? this)
               (= "login" (:command response)))
          (create-subshell! this :demo resp-msg)

          (and (subshell/active? this)
               (= (subshell/disconnect-command this) (:command response)))
          (disconnect-subshell! this resp-msg)

          :else resp-msg)))

(defn handle-disconnect
  [this response future]
  (when (and (subshell/not-active? this)
           (= (:disconnect-command this) (:command response)))
    (disconnect this future)))

(def behaviour
  {:banner banner
   :create-subshell! create-subshell!
   :disconnect-subshell! disconnect-subshell!
   :parse parse
   :render render
   :handle-request handle-request
   :handle-disconnect handle-disconnect})

(defn create-shell
  ([]
    (create-shell {}))
  ([opts]
    (map->LoginShell (merge {:disconnect-command "QUIT"
                             :grammar-type :login}
                            opts))))
