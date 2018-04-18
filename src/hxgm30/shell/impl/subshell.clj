(ns hxgm30.shell.impl.subshell
  (:require
    [taoensso.timbre :as log])
(:refer-clojure :exclude [set! type]))

(defrecord Subshell [
  type
  instance])

(def default-prompt " ")

(defn create
  ([parent]
    (create parent {}))
  ([parent {:keys [type] :as opts}]
    (let [shell-opts (select-keys opts [:disconnect-command :prompt])
          shell-type (:type opts)
          factory (shell-type (:legal-subshells parent))
          subshell-opts (select-keys opts [:type])]
      (->Subshell shell-type (factory shell-opts)))))

(defn not-active?
  [parent]
  (nil? @(:active-subshell parent)))

(defn set!
  [parent value]
  (reset! (:active-subshell parent) value))

(defn active?
  [parent]
  (not (not-active? parent)))

(defn disconnect-command
  [parent]
  (get-in @(:active-subshell parent) [:instance :disconnect-command]))

(defn instance
  [parent]
  (:instance @(:active-subshell parent)))

(defn prompt
  [parent]
  (or (get-in @(:active-subshell parent) [:instance :prompt])
      default-prompt))

(defn type
  [parent]
  (:type @(:active-subshell parent)))
