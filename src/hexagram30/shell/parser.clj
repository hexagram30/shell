(ns hexagram30.shell.parser
  (:require
    [clojure.string :as string]
    [hexagram30.shell.grammar :as grammar]))

(defrecord Result [
  command
  subcommands
  tail
  result-tmpl
  result-args])

(defn canonical-cmd
  [coll]
  (if-let [cmd (first coll)]
    (if (string? cmd)
        (assoc coll 0 (keyword cmd))
        coll)
    []))

(defn line->words
  [line]
  (string/split line #"\s+"))

(defn words->line
  [words]
  (string/join " " words))

(defn dispatch
  [cmd subcmds tail]
  (case cmd
    "" (->Result cmd subcmds nil "Please type something ...\n" nil)
    "bye" (->Result cmd subcmds nil "Good-bye\n" nil)
    "say" (->Result cmd
                    subcmds
                    tail
                    "You say: '%s'\n"
                    [(->> tail
                          (remove nil?)
                          (words->line))])))

(defn error
  [cmd subcmds tail]
  (if (seq subcmds)
    (->Result cmd
              subcmds
              nil
              "Error: command '%s' with subcommand(s) %s is not supported.\n"
              [cmd (vec subcmds)])
    (->Result cmd
              subcmds
              nil
              "Error: command '%s' not supported.\n"
              [cmd])))

(defn parse
  ([line]
    (parse grammar/default-command-tree line))
  ([command-grammar line]
    (let [args (line->words line)
          [cmd & subcmds :as cmds] (grammar/get-commands command-grammar args)
          tail (grammar/get-tail command-grammar args)]
      (if (grammar/validate command-grammar args)
        (dispatch cmd subcmds tail)
        (error cmd subcmds tail)))))
