(ns hxgm30.shell.reader.grammar
  (:require
    [clojure.string :as string]
    [hxgm30.registration.components.registrar]
    [hxgm30.shell.formatter :as formatter]
    [taoensso.timbre :as log])
  (:import
    (clojure.lang Keyword)))

(load "grammar/entry")

(def command-group-keys (cons :commands (repeat :subcommands)))

(def command-tree
  (merge
    entry-command-tree
    {}))

(defn has-shell?
  ([^Keyword shell]
    (has-shell? command-tree shell))
  ([cmd-tree ^Keyword shell]
    (not (nil? (shell cmd-tree)))))

(defn get-in-command
  ([^Keyword shell cmds]
    (get-in-command command-tree shell cmds))
  ([cmd-tree ^Keyword shell cmds]
    (get-in cmd-tree
            (cons shell
                  (interleave command-group-keys
                              (map keyword cmds))))))

(defn has-keys?
  [& args]
  (not (nil? (apply get-in-command args))))

(defn commands
  ([^Keyword shell]
    (commands shell {}))
  ([^Keyword shell opts]
    (commands command-tree shell opts))
  ([cmd-tree ^Keyword shell opts]
    (let [cmds (:commands (get-in-command cmd-tree shell []))]
      (cond (empty? cmds)
            cmds

            (true? (:as-keys opts))
            (keys cmds)

            (true? (:as-item-list opts))
            (formatter/list-items (map name (keys cmds)) opts)

            :else cmds))))

(defn command
  ([^Keyword shell ^Keyword cmd]
    (command command-tree shell cmd))
  ([cmd-tree ^Keyword shell ^Keyword cmd]
    (get-in-command cmd-tree shell [cmd])))

(defn has-command?
  ([^Keyword shell ^Keyword cmd]
    (has-command? command-tree shell cmd))
  ([cmd-tree ^Keyword shell ^Keyword cmd]
    (not (nil? (command cmd-tree shell cmd)))))

(defn command-help
  ([^Keyword shell ^Keyword cmd]
    (command-help command-tree shell cmd))
  ([cmd-tree ^Keyword shell ^Keyword cmd]
    (let [cmd-data (get-in-command command-tree shell [cmd])
          help-text (:help cmd-data)
          help-fn (:help-fn cmd-data)]
      (if (nil? help-text)
        ""
        (if (nil? help-fn)
          help-text
          (str help-text (help-fn)))))))

(defn command-fn
  ([^Keyword shell ^Keyword cmd]
    (command-fn command-tree shell cmd))
  ([cmd-tree ^Keyword shell ^Keyword cmd]
    (:fn (command cmd-tree shell cmd))))

(defn subcommands
  ([^Keyword shell ^Keyword cmd]
    (subcommands shell cmd []))
  ([^Keyword shell ^Keyword cmd subcmds]
    (subcommands shell cmd subcmds {}))
  ([^Keyword shell ^Keyword cmd subcmds opts]
    (subcommands command-tree shell cmd subcmds opts))
  ([cmd-tree ^Keyword shell ^Keyword cmd subcmds opts]
    (let [subcmds (:subcommands (get-in-command
                                 cmd-tree shell (cons cmd subcmds)))]
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
  (log/warn "args:" args)
  (not (nil? (apply subcommands args))))

(defn subcommands-keys
  [subcmds]
  (interleave (repeat :subcommands) (map keyword subcmds)))

(defn subcommand
  ([^Keyword shell ^Keyword cmd]
    (subcommand command-tree shell cmd []))
  ([^Keyword shell ^Keyword cmd subcmds]
    (subcommand command-tree shell cmd subcmds))
  ([cmd-tree ^Keyword shell ^Keyword cmd subcmds]
    (if (or (nil? subcmds) (empty? subcmds))
      (command-help cmd-tree shell cmd)
      (get-in-command cmd-tree shell (cons cmd subcmds)))))

(defn subcommand-help
  ([^Keyword shell ^Keyword cmd]
    (subcommand-help command-tree shell cmd []))
  ([^Keyword shell ^Keyword cmd subcmds]
    (subcommand-help command-tree shell cmd subcmds))
  ([cmd-tree ^Keyword shell ^Keyword cmd subcmds]
    (let [subcmd-data (get-in-command cmd-tree shell (cons cmd subcmds))
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
    (:fn (get-in-command cmd-tree shell (cons cmd subcmds)))))

(defn callable?
  ([^Keyword shell ^Keyword cmd]
    (callable? command-tree shell cmd []))
  ([^Keyword shell ^Keyword cmd subcmds]
    (callable? command-tree shell cmd subcmds))
  ([cmd-tree ^Keyword shell ^Keyword cmd subcmds]
    (if (nil? subcmds)
      (not (nil? (command-fn cmd-tree shell cmd)))
      (not (nil? (subcommand-fn cmd-tree shell cmd subcmds))))))
