(ns hxgm30.shell.evaluator.core
  (:require
    [hxgm30.shell.reader.grammar :as grammar])
  (:import
    (clojure.lang Keyword)))

(defn commands
  [^Keyword shell]
  (->> shell
       grammar/commands
       keys
       sort
       (map name)))
