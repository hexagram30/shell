(ns hxgm30.shell.impl.entry
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]
    [hxgm30.common.util :as util]
    [hxgm30.shell.evaluator :as evaluator]
    [hxgm30.shell.formatter :as formatter]
    [hxgm30.shell.impl.base :as base]
    [hxgm30.shell.reader.grammar :as grammar]
    [hxgm30.shell.reader.parser :as reader]
    [taoensso.timbre :as log])
  (:import
    (java.net InetAddress)
    (java.util Date))
  (:refer-clojure :exclude [read print]))

(defrecord EntryShell
  [disconnect-command
   grammar
   options])

(def commands-with-list-output #{:commands})

(defn prompt
  [this]
  "\r\ninterstices> ")

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

(defn connect-help
  [this]
  (str "To see a list of commands, type 'commands'; for help on any of the "
       "commands, type 'help <command name>'. To get help on a command's "
       "subcommand, 'help <command name> <subcommand name>', etc."))

(defn on-connect
  [this]
  (str (banner this)
       formatter/new-line
       (formatter/paragraph (motd this))
       (formatter/paragraph (connect-help this))
       formatter/new-line))

(defn read
  [this line]
  (log/debug "Reading command ...")
  (reader/parse (str "entry" \space line)))

(defn evaluate
  [this {:keys [shell cmd subcmds args] :as parsed}]
  (log/debug "Evaluating command ...")
  (cond (= :commands cmd)
        (evaluator/commands shell)

        (= :help cmd)
        ;; The cmd doesn't need to be passed, just the args (which are the
        ;; cmd/subcmds the user wants help on).
        (evaluator/help shell args)

        :else
        (into {} parsed)))

(defn print
  ([this evaled]
    (log/debug "Printing evaluated result ...")
    evaled)
  ([this {cmd :cmd} evaled]
    (log/debug "Printing evaluated result ...")
    evaled))

(defn handle-line
  [this line]
  (->> line
       (read this)
       (evaluate this)
       (print this)))

(def behaviour
  {:banner banner
   :connect-help connect-help
   :disconnect? base/disconnect?
   :evaluate evaluate
   :handle-line handle-line
   :motd motd
   :print print
   :prompt prompt
   :on-connect on-connect
   :read read})

(defn create
  ([]
    (create {}))
  ([opts]
    (map->EntryShell (merge base/default-options
                            {:grammar (:entry grammar/command-tree)
                             :options opts}))))
