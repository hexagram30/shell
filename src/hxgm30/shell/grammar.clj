(ns hxgm30.shell.grammar)

(def default-command-tree
  {"" true
   "bye" true
   "say" {:depth 1
          :any true}})

(def login-command-tree
  {"" true
   "QUIT" true
   "login" {:depth 1
            :any true}
   "create" {:depth 1
             :any true}})

(def demo-command-tree
  {"" true
   "logout" true
   "say" {:depth 1
          :any true}})

(def ^:dynamic *grammars*
  {:default default-command-tree
   :login login-command-tree
   :demo demo-command-tree})

(defn valid-child?
  [result]
  (cond (true? result) true
        (= :any result) true
        (:any result) true
        (not (nil? (:depth result))) true
        :else false))

(defn valid-parent?
  [result]
  (cond (= :any result) true
        (:any result) true
        :else false))

(defn depth-limit
  [grammar-key cmds]
  (let [full-length (count cmds)
        command-tree (grammar-key *grammars*)]
    (if-let [depth (:depth (get command-tree (first cmds)))]
      (if (> depth full-length)
        full-length
        depth)
      full-length)))

(defn get-commands
  [grammar-key cmds]
  (subvec cmds
          0
          (depth-limit grammar-key cmds)))

(defn get-tail
  [grammar-key cmds]
  (subvec cmds
          (depth-limit grammar-key cmds)
          (count cmds)))

(defn get-subtree
  [grammar-key cmds]
  (let [command-tree (grammar-key *grammars*)]
    (get-in command-tree
            (get-commands grammar-key cmds))))

(defn get-parent-subtree
  [grammar-key cmds]
  (get-in (grammar-key *grammars*) (butlast cmds)))

(defn validate
  "Given a collection consisting of a command, an optional sub-command, and
  that sub-commands optional sub-sub-command, etc., check to see if these are
  allowed by performing a nested lookup in the command tree data structure."
  ([cmds]
    (validate :default cmds))
  ([grammar-key cmds]
    (if (valid-child? (get-subtree grammar-key cmds))
      true
      (valid-parent? (get-parent-subtree grammar-key cmds)))))
