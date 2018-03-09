(ns hexagram30.shell.grammar)

(def default-command-tree
  {nil true
   "" true
   "bye" true
   "say" {
     :any true}})

(defn valid-child?
  [result]
  (cond (true? result) true
        (= :any result) true
        (:any result) true
        :else false))

(defn valid-parent?
  [result]
  (cond (= :any result) true
        (:any result) true
        :else false))

(defn validate
  "Given a collection consisting of a command, an optional sub-command, and
  that sub-commands optional sub-sub-command, etc., check to see if these are
  allowed by performing a nested lookup in the command tree data structure."
  ([cmds]
    (validate default-command-tree cmds))
  ([command-tree cmds]
    (let [child (get-in command-tree cmds)
          parent (get-in command-tree (butlast cmds))]
      (if (valid-child? child)
        true
        (valid-parent? parent)))))
