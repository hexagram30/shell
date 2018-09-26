(ns hxgm30.shell.reader.grammar.core
  (:require
    [clojure.string :as string]
    [hxgm30.shell.formatter :as formatter]
    [hxgm30.shell.reader.grammar.entry :as entry]
    [hxgm30.shell.util :as util]
    [taoensso.timbre :as log])
  (:import
    (clojure.lang Keyword)
    (hxgm30.shell.reader.grammar.entry EntryGrammar)))

(defn create
  [^Keyword grammar-type]
  (case grammar-type
    :entry (entry/create)))

(def command-group-keys (repeat :subcommands))

(defn get-in-command
  [grammar cmds]
  (let [g (:command-tree grammar)]
    (if (or (nil? cmds) (empty? cmds))
      g
      (get-in g
              (cons (first cmds)
                    (interleave command-group-keys
                                (map keyword (rest cmds))))))))

(defn has-keys?
  [& args]
  (not (nil? (apply get-in-command args))))

(defn commands
  ([grammar]
    (commands grammar {}))
  ([grammar opts]
    (let [cmds (get-in-command grammar [])]
      (cond (empty? cmds)
            cmds

            (true? (:as-keys opts))
            (keys cmds)

            (true? (:as-item-list opts))
            (formatter/list-items (map name (keys cmds)) opts)

            :else cmds))))

(defn command
  [grammar ^Keyword cmd]
  (get-in-command grammar [cmd]))

(defn has-command?
  [grammar ^Keyword cmd]
  (not (nil? (command grammar cmd))))

(defn fuzzy-command-keys
  [grammar ^Keyword cmd-attempt]
  (util/get-metaphone2 (:metaphones grammar) cmd-attempt))

(defn has-fuzzy-command?
  [grammar ^Keyword cmd-attempt]
  (seq (fuzzy-command-keys grammar cmd-attempt)))

(defn command-help
  [grammar ^Keyword cmd]
  (let [cmd-data (get-in-command grammar [cmd])
        help-text (:help cmd-data)
        help-fn (:help-fn cmd-data)]
    (if (nil? help-text)
      ""
      (if (nil? help-fn)
        help-text
        (str help-text (help-fn))))))

(defn command-fn
  [grammar ^Keyword cmd]
  (:fn (command grammar cmd)))

(defn subcommands
  ([grammar ^Keyword cmd]
    (subcommands grammar cmd []))
  ([grammar ^Keyword cmd subcmds]
    (subcommands grammar cmd subcmds {}))
  ([grammar ^Keyword cmd subcmds opts]
    (let [subcmds (:subcommands (get-in-command
                                 grammar
                                 (cons cmd subcmds)))]
      (cond (empty? opts)
            subcmds

            (true? (:as-item-list opts))
            (formatter/list-items (map name (keys subcmds)) opts)

            (true? (:as-keys opts))
            (keys subcmds)

            (true? (:comma-separated opts))
            (string/join ", " (map name (keys subcmds)))

            :else
            subcmds))))

(defn has-subcommands?
  [& args]
  (log/trace "args:" args)
  (not (nil? (apply subcommands args))))

(defn subcommands-keys
  [subcmds]
  (interleave (repeat :subcommands) (map keyword subcmds)))

(defn subcommand
  ([grammar ^Keyword cmd]
    (subcommand grammar cmd []))
  ([grammar ^Keyword cmd subcmds]
    (if (or (nil? subcmds) (empty? subcmds))
      (command-help grammar grammar cmd)
      (get-in-command grammar (cons cmd subcmds)))))

(defn subcommand-help
  ([grammar ^Keyword cmd]
    (subcommand-help grammar cmd []))
  ([grammar ^Keyword cmd subcmds]
    (let [subcmd-data (get-in-command grammar (cons cmd subcmds))
          help-text (:help subcmd-data)]
      (if-let [help-fn (:help-fn subcmd-data)]
        (str help-text (help-fn))
        help-text))))

(defn subcommand-fn
  ([grammar ^Keyword cmd]
    (subcommand-fn grammar cmd []))
  ([grammar ^Keyword cmd subcmds]
    (:fn (get-in-command grammar (cons cmd subcmds)))))

(defn callable?
  ([grammar ^Keyword cmd]
    (callable? grammar cmd []))
  ([grammar ^Keyword cmd subcmds]
    (if (nil? subcmds)
      (not (nil? (command-fn grammar cmd)))
      (not (nil? (subcommand-fn grammar cmd subcmds))))))
