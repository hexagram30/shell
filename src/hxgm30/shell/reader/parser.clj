(ns hxgm30.shell.reader.parser
  (:require
    [clojure.string :as string]
    [hxgm30.shell.evaluator.core :as evaluator]
    [hxgm30.shell.reader.grammar :as grammar]
    [taoensso.timbre :as log]))

(defn tokenize
  [line]
  (string/split line #"\s+"))

(defn assemble
  [words]
  (string/join " " words))

(defn subcommands
  [cmd-tree shell cmd subcmds-args]
  (->> (range (inc (count subcmds-args)))
              (map #(subvec (vec subcmds-args) 0 %))
              (take-while #(grammar/keys->subcommand cmd-tree shell cmd %))
              last
              (mapv keyword)))

(defn args
  [subcmds-args subcmds]
  (vec (nthrest subcmds-args (count subcmds))))

(defn subcommands+args
  ([shell cmd subcmds-args]
    (subcommands+args grammar/command-tree shell cmd subcmds-args))
  ([cmd-tree shell cmd subcmds-args]
    (let [subcmds (subcommands cmd-tree shell cmd subcmds-args)]
      {:subcommands (or subcmds [])
       :args (or (args subcmds-args subcmds) [])})))

(defn parse
  ([line]
    (tokenize grammar/command-tree line))
  ([cmd-tree line]
    (let [[str-shell str-cmd & rest-strs] (tokenize line)
          shell (keyword str-shell)
          cmd (keyword str-cmd)]
      (merge {:shell shell :cmd cmd}
             (subcommands+args cmd-tree shell cmd rest-strs)))))
