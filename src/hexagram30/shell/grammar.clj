(ns hexagram30.shell.grammar)

(def default-command-tree
  {"" true
   "bye" true
   "say" {:depth 1
          :any true}})

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
  [command-tree cmds]
  (let [full-length (count cmds)]
    (if-let [depth (:depth (get command-tree (first cmds)))]
      (if (> depth full-length)
        full-length
        depth)
      full-length)))

(defn get-commands
  [command-tree cmds]
  (subvec cmds
          0
          (depth-limit command-tree cmds)))

(defn get-tail
  [command-tree cmds]
  (subvec cmds
          (depth-limit command-tree cmds)
          (count cmds)))

(defn get-subtree
  [command-tree cmds]
  (get-in command-tree
          (get-commands command-tree cmds)))

(defn get-parent-subtree
  [command-tree cmds]
  (get-in command-tree (butlast cmds)))

(defn validate
  "Given a collection consisting of a command, an optional sub-command, and
  that sub-commands optional sub-sub-command, etc., check to see if these are
  allowed by performing a nested lookup in the command tree data structure."
  ([cmds]
    (validate default-command-tree cmds))
  ([command-tree cmds]
    (let [child (get-subtree command-tree cmds)]
      (if (valid-child? child)
        true
        (valid-parent? (get-parent-subtree command-tree cmds))))))
