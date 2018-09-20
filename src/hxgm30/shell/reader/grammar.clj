(ns hxgm30.shell.reader.grammar
  (:require
    [clojure.string :as string]
    [hxgm30.registration.components.registrar])
  (:import
    (clojure.lang Keyword)))

(load "grammar/entry")

(def command-tree
  (merge
    entry-command-tree
    {}))

(defn has-shell?
  ([^Keyword shell]
    (has-shell? command-tree shell))
  ([cmd-tree ^Keyword shell]
    (not (nil? (shell cmd-tree)))))

(defn commands
  ([^Keyword shell]
    (commands shell {}))
  ([^Keyword shell opts]
    (commands command-tree shell opts))
  ([cmd-tree ^Keyword shell opts]
    (let [cmds (get-in cmd-tree [shell :commands])]
      (if (:as-keys opts)
        (keys cmds)
        cmds))))

(defn has-command?
  ([^Keyword shell ^Keyword cmd]
    (has-command? command-tree shell cmd))
  ([cmd-tree ^Keyword shell ^Keyword cmd]
    (not (nil? (cmd (commands cmd-tree shell {}))))))

(defn command
  ([^Keyword shell ^Keyword cmd]
    (command command-tree shell cmd))
  ([cmd-tree ^Keyword shell ^Keyword cmd]
    (get-in cmd-tree [shell :commands cmd])))

(defn command-help
  ([^Keyword shell ^Keyword cmd]
    (command-help command-tree shell cmd))
  ([cmd-tree ^Keyword shell ^Keyword cmd]
    (get-in cmd-tree [shell :commands cmd :help])))

(defn command-help
  ([^Keyword shell ^Keyword cmd]
    (command-help command-tree shell cmd))
  ([cmd-tree ^Keyword shell ^Keyword cmd]
    (let [help-text (get-in command-tree
                            [shell :commands cmd :help])]
      (if-let [help-fn (:help-fn (command shell cmd))]
        (str help-text (help-fn))
        help-text))))

(defn command-fn
  ([^Keyword shell ^Keyword cmd]
    (command-fn command-tree shell cmd))
  ([cmd-tree ^Keyword shell ^Keyword cmd]
    (get-in cmd-tree [shell :commands cmd :fn])))

(defn has-subcommands?
  ([^Keyword shell ^Keyword cmd]
    (has-subcommands? command-tree shell cmd))
  ([cmd-tree ^Keyword shell ^Keyword cmd]
    (let [cmd-data (command cmd-tree shell cmd)]
      (not (nil? (:subcommands cmd-data))))))

(defn subcommands
  ([^Keyword shell ^Keyword cmd]
    (subcommands shell cmd {}))
  ([^Keyword shell ^Keyword cmd opts]
    (subcommands command-tree shell cmd opts))
  ([cmd-tree ^Keyword shell ^Keyword cmd opts]
    (let [subcmds (get-in cmd-tree [shell :commands cmd :subcommands])]
      (cond (true? (:as-keys opts))
            (keys subcmds)

            (true? (:comma-separated opts))
            (string/join ", "
                         (map name (keys subcmds)))

            :else
            subcmds))))

(defn subcommands-keys
  [subcmds]
  (interleave (repeat :subcommands) (map keyword subcmds)))

(defn keys->subcommand
  ([^Keyword shell ^Keyword cmd subcmds]
    (keys->subcommand command-tree shell cmd subcmds))
  ([cmd-tree ^Keyword shell ^Keyword cmd subcmds]
    (get-in cmd-tree
            (concat [shell :commands cmd]
                    (subcommands-keys subcmds)))))

(defn subcommand
  ([^Keyword shell ^Keyword cmd]
    (subcommand command-tree shell cmd []))
  ([^Keyword shell ^Keyword cmd subcmds]
    (subcommand command-tree shell cmd subcmds))
  ([cmd-tree ^Keyword shell ^Keyword cmd subcmds]
    (if (or (nil? subcmds) (empty? subcmds))
      (command-help cmd-tree shell cmd)
      (keys->subcommand cmd-tree shell cmd subcmds))))

(defn has-subcommand?
  [& args]
  (not (nil? (:subcommands (apply grammar/command args)))))

(defn subcommand-help
  ([^Keyword shell ^Keyword cmd]
    (subcommand-help command-tree shell cmd []))
  ([^Keyword shell ^Keyword cmd subcmds]
    (subcommand-help command-tree shell cmd subcmds))
  ([cmd-tree ^Keyword shell ^Keyword cmd subcmds]
    (let [subcmd-data (keys->subcommand cmd-tree shell cmd subcmds)
          help-text (:help subcmd-data)]
      (if-let [help-fn (:help-fn subcmd-data)]
        (str help-text (help-fn))
        help-text))))

(defn subcommand-fn
  ([^Keyword shell ^Keyword cmd]
    (subcommand-fn command-tree shell cmd []))
  ([^Keyword shell ^Keyword cmd subcmds]
    (subcommand-fn command-tree shell cmd subcmds))
  ([cmd-tree ^Keyword shell ^Keyword cmd subcmds]
    (:fn (keys->subcommand cmd-tree shell cmd subcmds))))

(defn callable?
  ([^Keyword shell ^Keyword cmd]
    (callable? command-tree shell cmd []))
  ([^Keyword shell ^Keyword cmd subcmds]
    (callable? command-tree shell cmd subcmds))
  ([cmd-tree ^Keyword shell ^Keyword cmd subcmds]
    (if (nil? subcmds)
      (not (nil? (command-fn cmd-tree shell cmd)))
      (not (nil? (subcommand-fn cmd-tree shell cmd subcmds))))))
