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
  [command-grammar args]
  (let [[cmd [:as subcmds]] (grammar/get-commands command-grammar args)
        tail (grammar/get-tail command-grammar args)]
    (case cmd
      "" (->Result cmd subcmds nil "Please type something ..." nil)
      "bye" (->Result cmd subcmds nil "Good-bye" nil)
      "say" (->Result cmd
                      subcmds
                      tail
                      "You say: '%s'"
                      (->> tail
                           (remove nil?)
                           (words->line))))))

(defn error
  [command-grammar args]
  (let [[cmd [:as subcmds]] (grammar/get-commands command-grammar args)]
    (if (seq args)
      (->Result cmd
                subcmds
                nil
                "Error: command '%s' with arguments '%s' is not supported."
                [cmd args])
      (->Result cmd
                subcmds
                nil
                "Error: command '%s' not supported."
                [cmd]))))

(defn parse
  ([line]
    (parse grammar/default-command-tree line))
  ([command-grammar line]
    (let [cmds (line->words line)]
      (if (grammar/validate command-grammar cmds)
        (dispatch command-grammar cmds)
        (error command-grammar cmds)))))
