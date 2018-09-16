(ns hxgm30.shell.reader.grammar
  (:require
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
  [^Keyword shell ^Keyword command]
  (get-in command-tree [shell :commands command]))

(defn command-help
  [^Keyword shell ^Keyword command]
  (get-in command-tree [shell :commands command :help]))

(defn command-fn
  [^Keyword shell ^Keyword command]
  (get-in command-tree [shell :commands command :fn]))

(defn subcommands
  ([^Keyword shell ^Keyword command]
    (subcommands shell command {}))
  ([^Keyword shell ^Keyword command opts]
    (let [subcmds (get-in command-tree [shell :commands command :subcommands])]
      (if (:as-keys opts)
        (keys subcmds)
        subcmds))))

(defn subcommand
  [^Keyword shell ^Keyword command ^Keyword subcommand]
  (get-in command-tree [shell :commands command :subcommands subcommand]))

(defn subcommand-help
  [^Keyword shell ^Keyword command ^Keyword subcommand]
  (get-in command-tree
          [shell :commands command :subcommands subcommand :help]))

(defn subcommand-fn
  [^Keyword shell ^Keyword command ^Keyword subcommand]
  (get-in command-tree
          [shell :commands command :subcommands subcommand :fn]))
