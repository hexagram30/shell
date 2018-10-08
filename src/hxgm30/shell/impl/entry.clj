(ns hxgm30.shell.impl.entry
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

(defrecord EntryShell
  [disconnect-command
   grammar
   parser
   options])

(def commands-with-list-output base/commands-with-list-output)

(defn prompt
  [this]
  "\r\n% interstitium> ")

(defn banner
  [this]
  (str formatter/new-line
       "Welcome to"
       (slurp (io/resource "text/banner.txt"))
       formatter/new-line
       "Running on "
       (.getHostName (InetAddress/getLocalHost))
       formatter/new-line
       "Current server time: "
       (new Date)
       formatter/new-line))

(defn motd
  [this]
  (str "You gaze around you in beliderment; rather than see, you feel the "
       "other worlds around you by means of a viceral twitching in your "
       "hands, almost as if the planetary globes are within reach. "
       "What you do actually see is impossible to describe, and lends itself "
       "more to the poetry of the abstract than visual perception; "
       "interstices of possibility, tendrils of stories yet to unfold ..."
       formatter/section-divider
       "You have connected to the top-level shell of a Hexagram 30 server. "
       "If you don't have a user account, regsiter to create one; "
       "if you do, you may create a new playing character or log in to the "
       "game world of your choice with a character you have already created."))

(defn on-connect
  [this]
  (str (banner this)
       formatter/new-line
       (formatter/paragraph (motd this))
       (formatter/paragraph (base/connect-help this))
       formatter/new-line
       formatter/new-line))

(defn evaluate
  ([this parsed]
    (evaluate nil parsed))
  ([this session-id {:keys [cmd subcmds args] :as parsed}]
    (log/debugf "Evaluating command (session-id: %s) ..." session-id)
    (let [gmr (:grammar this)]
      (cond (not (grammar/has-command? gmr cmd))
            (evaluator/no-command gmr cmd)

            (= :commands cmd)
            (evaluator/commands gmr)

            (= :help cmd)
            ;; The cmd doesn't need to be passed, just the args (which are the
            ;; cmd/subcmds the user wants help on).
            (evaluator/help gmr args)

            (= :login cmd)
            :not-implemented

            (= :register cmd)
            (apply (grammar/command-fn gmr cmd) args)

            (= :reset cmd)
            (apply (grammar/command-fn gmr cmd) args)

            ;; Note that quit is handled by the terminal app, since it is
            ;; responsible for closing the connection.

            :else
            (into {} parsed)))))

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
    (let [entry-grammar (grammar/create :entry)]
    (map->EntryShell (merge base/default-options
                            {:grammar entry-grammar
                             :parser (parser/create (:parser opts))
                             :options opts})))))
