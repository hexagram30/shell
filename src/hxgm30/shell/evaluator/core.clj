(ns hxgm30.shell.evaluator.core)

(defrecord Request [
  data
  grammar-key])

(defrecord Response [
  command
  subcommands
  tail
  result-tmpl
  result-args])

;; XXX Dispatch and error functions are a big mess: too much implementation
;;     specific stuff here; probbaly need some command abstractions
;; XXX Each shell should have its own dispatcher, not one for all (too much
;;     possibility of collision)
(defn dispatch
  [grammar-key cmd subcmds tail]
  (condp = cmd
    "" (->Response cmd subcmds nil "Please type something ...\n" nil)
    ;; XXX We'll fix this redundancy problem once we have command dispatch
    ;;     For now, though, this let's us not pass arbitrary command names
    ;;     to the parser from the shell, which was circumventing the grammar
    "bye" (->Response cmd subcmds nil "Good-bye\n" nil)
    "logout" (->Response cmd subcmds nil "Good-bye\n" nil)
    "QUIT" (->Response cmd subcmds nil "Good-bye\n" nil)
    "say" (->Response cmd subcmds tail "You say: '%s'\n" [tail])
    "logout" (->Response cmd subcmds tail "Logging out ...")
    "login" (->Response cmd subcmds tail "Logging in with: %s" [tail])
    "create" (->Response cmd subcmds tail "Creating: %s" [tail])))

(defn error
  [grammar-key cmd subcmds tail]
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
