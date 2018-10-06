(ns hxgm30.shell.impl.player
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]
    [hxgm30.common.util :as util]
    [hxgm30.shell.errors :as errors]
    [hxgm30.shell.evaluator :as evaluator]
    [hxgm30.shell.formatter :as formatter]
    [hxgm30.shell.impl.base :as base]
    [hxgm30.shell.reader.grammar.common :as common-grammar]
    [hxgm30.shell.reader.grammar.core :as grammar]
    [hxgm30.shell.reader.parser.base :as parser]
    [taoensso.timbre :as log])
  (:import
    (java.net InetAddress)
    (java.util Date))
  (:refer-clojure :exclude [read print]))

(defrecord PlayerShell
  [disconnect-command
   grammar
   parser
   options])

(def commands-with-list-output base/commands-with-list-output)

(defn prompt
  [this]
  "\r\n% player> ")

(defn banner
  [this]
  "")

(defn motd
  [this]
  (str "Behind you are the interstices of all worlds; you have stepped out "
       "into one of these that provides a better view into the connected "
       "worlds ... and the means by which you may interact with them."))

(defn on-connect
  [this]
  (str (banner this)
       formatter/new-line
       (formatter/paragraph (motd this))
       (formatter/paragraph (base/connect-help this))
       formatter/new-line
       formatter/new-line))

(defn evaluate
  [this {:keys [cmd subcmds args] :as parsed}]
  (log/debug "Evaluating command ...")
  (let [gmr (:grammar this)]
    (cond (not (grammar/has-command? gmr cmd))
          (evaluator/no-command gmr cmd)

          (= :commands cmd)
          (evaluator/commands gmr)

          (= :help cmd)
          ;; The cmd doesn't need to be passed, just the args (which are the
          ;; cmd/subcmds the user wants help on).
          (evaluator/help gmr args)

          ;; Note that quit is handled by the terminal app, since it is
          ;; responsible for closing the connection.

          :else
          (into {} parsed))))

(defn handle-line
  [this line]
  (->> line
       (base/read this)
       (evaluate this)
       (base/print this)))

(def behaviour
  {:banner banner
   :connect-help base/connect-help
   :disconnect? base/disconnect?
   :evaluate evaluate
   :handle-line handle-line
   :motd motd
   :print base/print
   :prompt prompt
   :on-connect on-connect
   :read base/read})

(defn create
  ([]
    (create {}))
  ([opts]
    (let [player-grammar (grammar/create :player)]
    (map->PlayerShell (merge base/default-options
                             {:grammar player-grammar
                              :parser (parser/create (:parser opts))
                              :options opts})))))
