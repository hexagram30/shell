(ns hxgm30.shell.parser
  (:require
    [clojure.string :as string]
    [hxgm30.shell.grammar :as grammar]))

(defrecord Request [
  data
  grammar-key
  disconnect-command])

(defrecord Response [
  command
  subcommands
  tail
  result-tmpl
  result-args])

(defn line->words
  [line]
  (string/split line #"\s+"))

(defn words->line
  [words]
  (string/join " " words))

;; XXX Dispatch and error functions are a big mess: too much implementation
;;     specific stuff here; probbaly need some command abstractions
;; XXX Each shell should have its own dispatcher, not one for all (too much
;;     possibility of collision)
(defn dispatch
  [disconnect-command cmd subcmds tail]
  (condp = cmd
    "" (->Response cmd subcmds nil "Please type something ...\n" nil)
    disconnect-command (->Response cmd subcmds nil "Good-bye\n" nil)
    "say" (->Response cmd
                      subcmds
                      tail
                      "You say: '%s'\n"
                      [(->> tail
                            (remove nil?)
                            (words->line))])
    "logout" (->Response cmd subcmds tail "Logging out ...")
    "login" (->Response cmd subcmds tail "Logging in with: %s" [tail])
    "create" (->Response cmd subcmds tail "Creating: %s" [tail])))

(defn error
  [cmd subcmds tail]
  (if (seq subcmds)
    (->Response cmd
                subcmds
                nil
                "Error: command '%s' with subcommand(s) %s is not supported.\n"
                [cmd (vec subcmds)])
    (->Response cmd
                subcmds
                nil
                "Error: command '%s' not supported.\n"
                [cmd])))

(defn parse
  ([disconnect-command line]
    (parse grammar/default-command-tree disconnect-command line))
  ([command-grammar disconnect-command line]
    (let [args (line->words line)
          [cmd & subcmds :as cmds] (grammar/get-commands command-grammar args)
          tail (grammar/get-tail command-grammar args)]
      (if (grammar/validate command-grammar args)
        (dispatch disconnect-command cmd subcmds tail)
        (error cmd subcmds tail)))))
