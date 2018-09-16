(ns hxgm30.shell.reader.grammar
  (:require
    [clojure.string :as string]
    [hxgm30.registration.components.registrar])
  (:import
    (clojure.lang Keyword)))

; (load "grammar/admin")
; (load "grammar/demo")
(load "grammar/entry")
; (load "grammar/login")
; (load "grammar/player")
; (load "grammar/registration")

; (def ^:dynamic *grammars*
;   {:admin admin-command-tree
;    :demo demo-command-tree
;    :login login-command-tree
;    :player player-command-tree
;    :registration registration-command-tree})

; (defn valid-child?
;   [result]
;   (cond (true? result) true
;         (= :any result) true
;         (:any result) true
;         (not (nil? (:depth result))) true
;         :else false))

; (defn valid-parent?
;   [result]
;   (cond (= :any result) true
;         (:any result) true
;         :else false))

; (defn depth-limit
;   [grammar-key cmds]
;   (let [full-length (count cmds)
;         command-tree (grammar-key *grammars*)]
;     (if-let [depth (:depth (get command-tree (first cmds)))]
;       (if (> depth full-length)
;         full-length
;         depth)
;       full-length)))

; (defn get-commands
;   [grammar-key cmds]
;   (subvec cmds
;           0
;           (depth-limit grammar-key cmds)))

; (defn get-tail
;   [grammar-key cmds]
;   (subvec cmds
;           (depth-limit grammar-key cmds)
;           (count cmds)))

; (defn get-subtree
;   [grammar-key cmds]
;   (let [command-tree (grammar-key *grammars*)]
;     (get-in command-tree
;             (get-commands grammar-key cmds))))

; (defn get-parent-subtree
;   [grammar-key cmds]
;   (get-in (grammar-key *grammars*) (butlast cmds)))

; (defn validate
;   "Given a collection consisting of a command, an optional sub-command, and
;   that sub-commands optional sub-sub-command, etc., check to see if these are
;   allowed by performing a nested lookup in the command tree data structure."
;   ([cmds]
;     (validate :default cmds))
;   ([grammar-key cmds]
;     (if (valid-child? (get-subtree grammar-key cmds))
;       true
;       (valid-parent? (get-parent-subtree grammar-key cmds)))))

(def command-tree
  (merge
    entry-command-tree
    {}))

(defn commands
  ([^Keyword shell]
    (commands shell {}))
  ([^Keyword shell opts]
    (let [cmds (get-in command-tree [shell :commands])]
      (if (:as-keys opts)
        (keys cmds)
        cmds))))

(defn command
  [^Keyword shell ^Keyword cmd]
  (get-in command-tree [shell :commands cmd]))

(defn command-help
  [^Keyword shell ^Keyword cmd]
  (get-in command-tree [shell :commands cmd :help]))

(defn command-help
  [^Keyword shell ^Keyword cmd]
  (let [help-text (get-in command-tree
                          [shell :commands cmd :help])]
    (if-let [help-fn (:help-fn (command shell cmd))]
      (str help-text (help-fn))
      help-text)))

(defn command-fn
  [^Keyword shell ^Keyword cmd]
  (get-in command-tree [shell :commands cmd :fn]))

(defn has-subcommands?
  [^Keyword shell ^Keyword cmd]
  (let [cmd-data (command shell cmd)]
    (not (nil? (:subcommands cmd-data)))))

(defn subcommands
  ([^Keyword shell ^Keyword cmd]
    (subcommands shell cmd {}))
  ([^Keyword shell ^Keyword cmd opts]
    (let [subcmds (get-in command-tree [shell :commands cmd :subcommands])]
      (cond (true? (:as-keys opts))
            (keys subcmds)

            (true? (:comma-separated opts))
            (string/join ", "
                         (map name (keys subcmds)))

            :else
            subcmds))))

;; XXX The following are going to have to be reworked/updated to support deeply
;;     nested subcommands ...

(defn subcommand
  [^Keyword shell ^Keyword cmd ^Keyword subcmd]
  (get-in command-tree [shell :commands cmd :subcommands subcmd]))

(defn subcommand-help
  [^Keyword shell ^Keyword cmd ^Keyword subcmd]
  (let [help-text (get-in command-tree
                          [shell :commands cmd :subcommands subcmd :help])]
    (if-let [help-fn (:help-fn (subcommand shell cmd subcmd))]
      (str help-text (help-fn))
      help-text)))

(defn subcommand-fn
  [^Keyword shell ^Keyword cmd ^Keyword subcmd]
  (get-in command-tree
          [shell :commands cmd :subcommands subcmd :fn]))

(defn callable?
  ([^Keyword shell ^Keyword cmd]
    (not (nil? (command-fn shell cmd))))
  ([^Keyword shell ^Keyword cmd ^Keyword subcmd]
    (not (nil? (subcommand-fn shell cmd subcmd)))))
