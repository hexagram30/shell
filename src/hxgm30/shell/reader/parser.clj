(ns hxgm30.shell.reader.parser
  (:require
    [clojure.string :as string]
    [hxgm30.shell.evaluator.core :as evaluator]
    [hxgm30.shell.reader.grammar :as grammar]))

(defn tokenize
  [line]
  (let [[str-shell str-cmd & rest-strs] (string/split line #"\s+")
        shell (keyword str-shell)
        cmd (keyword str-cmd)]
    (concat [shell cmd] rest-strs)))


(defn assemble
  [words]
  (string/join " " words))

(defn parse
  ([line]
    (apply parse (tokenize line)))
  ([shell cmd & args]
    (println "shell: " shell)
    (println "cmd: " cmd)
    (println "args: " args)))
