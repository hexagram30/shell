(ns hexagram30.shell.grammar-test
  (:require
    [clojure.test :refer :all]
    [hexagram30.shell.grammar :as grammar]))

(def test-command-tree1 {
  nil true
  "bye" true})

(deftest validate-simple-true
  (is (= true
         (grammar/validate test-command-tree1 [nil])))
  (is (= true
         (grammar/validate test-command-tree1 ["bye"]))))

(deftest validate-simple-false
  (is (= false
         (grammar/validate test-command-tree1 nil)))
  (is (= false
         (grammar/validate test-command-tree1 [])))
  (is (= false
         (grammar/validate test-command-tree1 ["bye" "more"])))
  (is (= false
         (grammar/validate test-command-tree1 ["bad"])))
  (is (= false
         (grammar/validate test-command-tree1 ["bad" "bye"]))))

(def test-command-tree2 {
  "ps" {"aux" true}
  "ls" {
    :any true
    "-al" {:any true}
    "-a" {:any true}}
 "rm" {
  "-rf" {"/tmp/allowed-dir" true}}})

(deftest validate-nested-true
  (is (= true
         (grammar/validate test-command-tree2 ["ps" "aux"])))
  (is (= true
         (grammar/validate test-command-tree2 ["ls"])))
  (is (= true
         (grammar/validate test-command-tree2 ["ls" "file.txt"])))
  (is (= true
         (grammar/validate test-command-tree2 ["ls" "-al"])))
  (is (= true
         (grammar/validate test-command-tree2 ["ls" "-al" "file.txt"])))
  (is (= true
         (grammar/validate test-command-tree2 ["ls" "-a" "file.txt"])))
  (is (= true
         (grammar/validate test-command-tree2 ["rm" "-rf" "/tmp/allowed-dir"]))))

(deftest validate-nested-false
  (is (= false
         (grammar/validate test-command-tree2 ["ps"])))
  (is (= false
         (grammar/validate test-command-tree2 ["ps" "aux" "thing"])))
  (is (= false
         (grammar/validate test-command-tree2 ["ps" "-ef"])))
  (is (= false
         (grammar/validate test-command-tree2 ["ls" "-l" "file.txt"])))
  (is (= false
         (grammar/validate test-command-tree2 ["rm"])))
  (is (= false
         (grammar/validate test-command-tree2 ["rm" "-rf"])))
  (is (= false
         (grammar/validate test-command-tree2 ["rm" "-rf" "file.txt"]))))
