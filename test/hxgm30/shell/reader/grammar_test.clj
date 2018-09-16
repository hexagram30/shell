(ns hxgm30.shell.reader.grammar-test
  (:require
    [clojure.test :refer :all]
    [hxgm30.shell.reader.grammar :as grammar]))

; (def test-command-tree1 {
;   "bye" true})

; (def test-command-tree2 {
;   "ps" {"aux" true}
;   "ls" {:any true
;         "-al" {:any true}
;         "-a" {:any true}}
;  "rm" {"-rf" {"/tmp/allowed-dir" true}}})

; (def test-command-tree3 {
;   "cmd1" {:depth 1}
;   "cmd2" {:depth 2 "subcmd1" true}
;   "cmd3" {:depth 3 "subcmd1" {"subcmd2" true}}
;   "cmd4" {:depth 3 "subcmd1" {"subcmd2" {:any true}}}})

; (def test-grammars
;   {:tree1 test-command-tree1
;    :tree2 test-command-tree2
;    :tree3 test-command-tree3})

; (defn grammar-fixture
;   [test-fn]
;   (let [orig-grammars (into {} grammar/*grammars*)]
;     (alter-var-root #'grammar/*grammars*
;                     (constantly test-grammars))
;     (test-fn)
;     (alter-var-root #'grammar/*grammars*
;                     (constantly orig-grammars))))

; (use-fixtures :once grammar-fixture)

; (deftest validate-simple-true
;   (is (= true
;          (grammar/validate :tree1 ["bye"]))))

; (deftest validate-simple-false
;   (is (= false
;          (grammar/validate :tree1 [])))
;   (is (= false
;          (grammar/validate :tree1 ["bye" "more"])))
;   (is (= false
;          (grammar/validate :tree1 ["bad"])))
;   (is (= false
;          (grammar/validate :tree1 ["bad" "bye"]))))

; (deftest validate-nested-true
;   (is (= true
;          (grammar/validate :tree2 ["ps" "aux"])))
;   (is (= true
;          (grammar/validate :tree2 ["ls"])))
;   (is (= true
;          (grammar/validate :tree2 ["ls" "file.txt"])))
;   (is (= true
;          (grammar/validate :tree2 ["ls" "-al"])))
;   (is (= true
;          (grammar/validate :tree2 ["ls" "-al" "file.txt"])))
;   (is (= true
;          (grammar/validate :tree2 ["ls" "-a" "file.txt"])))
;   (is (= true
;          (grammar/validate :tree2 ["rm" "-rf" "/tmp/allowed-dir"]))))

; (deftest validate-nested-false
;   (is (= false
;          (grammar/validate :tree2 ["ps"])))
;   (is (= false
;          (grammar/validate :tree2 ["ps" "aux" "thing"])))
;   (is (= false
;          (grammar/validate :tree2 ["ps" "-ef"])))
;   (is (= false
;          (grammar/validate :tree2 ["ls" "-l" "file.txt"])))
;   (is (= false
;          (grammar/validate :tree2 ["rm"])))
;   (is (= false
;          (grammar/validate :tree2 ["rm" "-rf"])))
;   (is (= false
;          (grammar/validate :tree2 ["rm" "-rf" "file.txt"]))))

; (deftest depth-limit
;   ;; test-command-tree1
;   (is (= 1 (grammar/depth-limit :tree1
;             ["bye"])))
;   (is (= 3 (grammar/depth-limit :tree1
;             ["bye" "bye" "birdie"])))
;   ;; test-command-tree3
;   (is (= 1 (grammar/depth-limit :tree3
;             ["cmd1"])))
;   (is (= 1 (grammar/depth-limit :tree3
;             ["cmd1" "x" "y" "z"])))
;   (is (= 1 (grammar/depth-limit :tree3
;             ["cmd2"])))
;   (is (= 2 (grammar/depth-limit :tree3
;             ["cmd2" "subcmd1"])))
;   (is (= 2 (grammar/depth-limit :tree3
;             ["cmd2" "subcmd1" "thing"])))
;   (is (= 1 (grammar/depth-limit :tree3
;             ["cmd3"])))
;   (is (= 2 (grammar/depth-limit :tree3
;             ["cmd3" "subcmd1"])))
;   (is (= 3 (grammar/depth-limit :tree3
;             ["cmd3" "subcmd1" "subcmd2"])))
;   (is (= 3 (grammar/depth-limit :tree3
;             ["cmd4" "subcmd1" "subcmd2" "and" "more" "things"]))))

; (deftest get-commands
;   ;; test-command-tree1
;   (is (= ["bye"]
;          (grammar/get-commands :tree1
;           ["bye"])))
;   (is (= ["bye" "bye" "birdie"]
;          (grammar/get-commands :tree1
;           ["bye" "bye" "birdie"])))
;   ;; test-command-tree3
;   (is (= ["cmd1"]
;          (grammar/get-commands :tree3
;           ["cmd1"])))
;   (is (= ["cmd1"]
;          (grammar/get-commands :tree3
;           ["cmd1" "x" "y" "z"])))
;   (is (= ["cmd2"]
;          (grammar/get-commands :tree3
;           ["cmd2"])))
;   (is (= ["cmd2" "subcmd1"]
;          (grammar/get-commands :tree3
;           ["cmd2" "subcmd1"])))
;   (is (= ["cmd2" "subcmd1"]
;          (grammar/get-commands :tree3
;           ["cmd2" "subcmd1" "thing"])))
;   (is (= ["cmd3"]
;          (grammar/get-commands :tree3
;           ["cmd3"])))
;   (is (= ["cmd3" "subcmd1"]
;          (grammar/get-commands :tree3
;           ["cmd3" "subcmd1"])))
;   (is (= ["cmd3" "subcmd1" "subcmd2"]
;          (grammar/get-commands :tree3
;           ["cmd3" "subcmd1" "subcmd2"])))
;   (is (= ["cmd4" "subcmd1" "subcmd2"]
;          (grammar/get-commands :tree3
;           ["cmd4" "subcmd1" "subcmd2" "and" "more" "things"]))))

; (deftest get-tail
;   (is (= []
;          (grammar/get-tail :tree3
;           ["cmd1"])))
;   (is (= ["x" "y" "z"]
;          (grammar/get-tail :tree3
;           ["cmd1" "x" "y" "z"])))
;   (is (= []
;          (grammar/get-tail :tree3
;           ["cmd3" "subcmd1" "subcmd2"])))
;   (is (= ["and" "yet" "more" "things"]
;          (grammar/get-tail :tree3
;           ["cmd4" "subcmd1" "subcmd2" "and" "yet" "more" "things"]))))

; (deftest get-subtree
;   ;; test-command-tree1
;   (is (= true
;          (grammar/get-subtree :tree1
;           ["bye"])))
;   (is (= nil
;          (grammar/get-subtree :tree1
;           ["bye" "bye" "birdie"])))
;   ;; test-command-tree3
;   (is (= {:depth 1}
;          (grammar/get-subtree :tree3
;           ["cmd1"]))))

; (deftest validate-nested-depth-check-true
;   (is (= true
;          (grammar/validate :tree3
;                            ["cmd1"])))
;   (is (= true
;          (grammar/validate :tree3
;                            ["cmd1" "the" "rest" "is" "ignored"])))
;   (is (= true
;          (grammar/validate :tree3
;                            ["cmd2" "subcmd1"])))
;   (is (= true
;          (grammar/validate :tree3
;                            ["cmd2" "subcmd1" "the" "rest" "is" "ignored"]))))

; (deftest validate-nested-depth-check-false
;   (is (= false
;          (grammar/validate :tree3
;                            ["cmd"])))
;   (is (= false
;          (grammar/validate :tree3
;                            ["cmd2" "the" "rest" "is" "ignored"])))
;   (is (= false
;          (grammar/validate :tree3
;                            ["cmd2" "subcmd" "the" "rest" "is" "ignored"]))))

(deftest commands
  (is (= [:help :login :register :reset]
         (sort (keys (grammar/commands :entry)))))
  (is (= [:help :login :register :reset]
         (sort (grammar/commands :entry {:as-keys true})))))

(deftest command
  (is (= {:help (str "Create a user account. Takes one argument, the user name. "
                     "The user will then be prompted to enter a password.")
          :fn #'hxgm30.registration.components.registrar/create-user}
         (grammar/command :entry :register)))
  (is (= "Perform one or more type of account resets."
         (grammar/command-help :entry :reset)))
  (is (= #'hxgm30.registration.components.registrar/create-user
         (grammar/command-fn :entry :register))))

(deftest has-subcommands?
  (is (not (grammar/has-subcommands? :entry :login)))
  (is (not (grammar/has-subcommands? :entry :register)))
  (is (grammar/has-subcommands? :entry :reset)))

(deftest subcommands
  (is (= [:password :player-key]
         (sort (keys (grammar/subcommands :entry :reset)))))
    (is (= [:password :player-key]
         (sort (grammar/subcommands :entry :reset {:as-keys true})))))

(deftest subcommand
  (is (= {:help "Reset the password for a given account."
          :fn #'hxgm30.registration.components.registrar/reset-password}
         (grammar/subcommand :entry :reset :password)))
  (is (= "Reset the player key for a given account."
         (grammar/subcommand-help :entry :reset :player-key)))
  (is (= #'hxgm30.registration.components.registrar/reset-player-key
         (grammar/subcommand-fn :entry :reset :player-key))))
