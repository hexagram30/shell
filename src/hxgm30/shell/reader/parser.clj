(ns hxgm30.shell.reader.parser
  (:require
    [clojure.string :as string]
    [hxgm30.shell.evaluator.core :as evaluator]
    [hxgm30.shell.reader.grammar :as grammar]))

;; rename to tokenize
(defn line->words
  [line]
  (string/split line #"\s+"))

(defn words->line
  [words]
  (string/join " " words))

(defn parse
  ([line]
    (parse :default line))
  ([grammar-key line]
    (let [args (line->words line)
          [cmd & subcmds :as cmds] (grammar/get-commands grammar-key args)
          tail (grammar/get-tail grammar-key args)]
      (if (grammar/validate grammar-key args)
        (evaluator/dispatch grammar-key cmd subcmds tail)
        (evaluator/error grammar-key cmd subcmds tail)))))
