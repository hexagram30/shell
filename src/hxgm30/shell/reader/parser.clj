(ns hxgm30.shell.reader.parser
  (:require
    [clojure.string :as string]
    [hxgm30.shell.reader.grammar.core :as grammar]
    [taoensso.timbre :as log]))

(defrecord Parsed
  [cmd
   subcmds
   args])

(defn tokenize
  [line]
  (string/split line #"\s+"))

(defn assemble
  [words]
  (string/join " " words))

(defn subcommands
  [grammar cmd subcmds-args]
  (->> (range (inc (count subcmds-args)))
              (map #(subvec (vec subcmds-args) 0 %))
              (take-while #(grammar/get-in-command grammar (cons cmd %)))
              last
              (mapv keyword)))

(defn args
  [subcmds-args subcmds]
  (vec (nthrest subcmds-args (count subcmds))))

(defn subcommands+args
  [grammar cmd subcmds-args]
  (let [subcmds (subcommands grammar cmd subcmds-args)]
    {:subcmds (or subcmds [])
     :args (or (args subcmds-args subcmds) [])}))

(defn parse
  [grammar line]
  (let [[str-cmd & rest-strs] (tokenize line)
        cmd (when (seq str-cmd) (keyword str-cmd))]
    (map->Parsed
      (merge {:cmd cmd}
             (subcommands+args grammar cmd rest-strs)))))
