(ns hxgm30.shell.reader.parser
  (:require
    [clojure.string :as string]
    [hxgm30.shell.reader.grammar.core :as grammar]
    [taoensso.timbre :as log]))

(defrecord Parsed
  [shell
   cmd
   subcmds
   args])

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
              (take-while #(grammar/get-in-command cmd-tree shell (cons cmd %)))
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
      {:subcmds (or subcmds [])
       :args (or (args subcmds-args subcmds) [])})))

(defn parse
  ([line]
    (parse grammar/command-tree line))
  ([cmd-tree line]
    (let [[str-shell str-cmd & rest-strs] (tokenize line)
          shell (keyword str-shell)
          cmd (keyword str-cmd)]
      (map->Parsed
        (merge {:shell shell :cmd cmd}
               (subcommands+args cmd-tree shell cmd rest-strs))))))
