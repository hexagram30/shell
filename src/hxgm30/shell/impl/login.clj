(ns hxgm30.shell.impl.login
  (:require
    [clojure.java.io :as io]
    [hxgm30.shell.grammar :as grammar]
    [hxgm30.shell.impl.base :as base]
    [hxgm30.shell.impl.demo :as demo]
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

(defrecord Subshell [
  type
  instance])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   WithSubshellAPI   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; XXX maybe put these in a subshell ns?

(defn create-subshell
  ([parent]
    (create-subshell parent {}))
  ([parent {:keys [type] :as opts}]
    (let [shell-opts (select-keys opts [:disconnect-command :prompt])
          shell-type (:type opts)
          factory (shell-type (:legal-subshells parent))
          subshell-opts (select-keys opts [:type])]
      (->Subshell shell-type (factory shell-opts)))))

(defn not-subshell?
  [parent]
  (nil? @(:active-subshell parent)))

(defn set-subshell!
  [parent value]
  (reset! (:active-subshell parent) value))

(defn subshell?
  [parent]
  (not (not-subshell? parent)))

(defn subshell-disconnect-command
  [parent]
  (get-in @(:active-subshell parent) [:instance :disconnect-command]))

(defn subshell-instance
  [parent]
  (:instance @(:active-subshell parent)))

(defn subshell-prompt
  [parent]
  (get-in @(:active-subshell parent) [:instance :prompt]))

(defn subshell-type
  [parent]
  (:type @(:active-subshell parent)))

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
     (if (subshell? this)
       (or (subshell-prompt this) "")
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
      (set-subshell! this (create-subshell this subshell-opts)))
    (when-not (nil? resp-msg)
      (log/debug "resp-msg:" resp-msg)
      (render this (:response resp-msg)))
    resp-msg))

(defn disconnect-subshell!
  [this resp-msg]
  (log/debug "Disconnecting from subshell ...")
  (set-subshell! this nil)
  resp-msg)

(defn banner
  [this]
  ;; XXX the file path for this should be pulled from configuration
  (slurp (io/resource "text/shell-login-banner.txt")))

(defn disconnect
  [this & args]
  (if (subshell? this)
    (disconnect-subshell! this)
    (apply (:disconnect-handler this) args)))

(defn parse
  [this request]
  (if (subshell? this)
    (parser/parse (grammar/grammars (subshell-type this))
                  (:disconnect-command (subshell-instance this))
                  request)
    (parser/parse (grammar/grammars :login)
                  (:disconnect-command this)
                  request)))

(defn handle-request
  [this request]
  (let [response (parse this request)
        _ (log/warn "Got response:" response)
        resp-msg {:response response
                  :message (render this response)}]
    (cond (and (not-subshell? this)
               (= "login" (:command response)))
          (create-subshell! this :demo resp-msg)

          (and (subshell? this)
               (= (subshell-disconnect-command this) (:command response)))
          (disconnect-subshell! this resp-msg)

          :else resp-msg)))

(defn handle-disconnect
  [this response future]
  (when (and (not-subshell? this)
           (= (:disconnect-command this) (:command response)))
    (disconnect this future))
  #_(when (and (subshell? this)
             (= (subshell-disconnect-command this) (:command response)))
    (render response)
    (disconnect-subshell! this)))

(def behaviour
  {:banner banner
   :create-subshell! create-subshell!
   :disconnect-subshell! disconnect-subshell!
   :parse parse
   :render render
   :handle-request handle-request
   :handle-disconnect handle-disconnect})

;; XXX move into subshell ns
(def with-subshell-behaviour
  {:not-subshell? not-subshell?
   :subshell? subshell?
   :subshell-disconnect-command subshell-disconnect-command
   :subshell-prompt subshell-prompt})

(defn create-shell
  ([]
    (create-shell {}))
  ([opts]
    (map->LoginShell (merge {:disconnect-command "QUIT"
                             :grammar-type :login}
                            opts))))
