(ns hxgm30.shell.reader.parser-test
  (:require
    [clojure.string :as string]
    [clojure.test :refer :all]
    [hxgm30.shell.reader.grammar.core :as grammar]
    [hxgm30.shell.reader.parser :as parser]))

(def test-deep-command-tree
  {:test-shell {
     :commands {
       :cmd1 {:data :for-cmd-1}
       :cmd2 {
         :subcommands {
           :scmd1 {
             :subcommands {
               :sscmd1 {:data :for-sscmd-1}
               :sscmd2 {:data :for-sscmd-2}
               :sscmd3 {
                 :subcommands {
                   :ssscmd1 {:data :for-ssscmd-1}
                   :ssscmd2 {:data :for-ssscmd-2}}}}}
           :scmd2 {:data :for-scmd-2}
           :scmd3 {:data :for-scmd-3}}}
       :cmd3 {:data :for-cmd-3}}}})

(deftest subcommands+args
  (is (not
        (nil?
          (parser/subcommands+args test-deep-command-tree
                                   :entry
                                   :login
                                   nil))))
  (is (not
        (nil?
          (parser/subcommands+args test-deep-command-tree
                                   :entry
                                   :login
                                   []))))
  (is (= {:subcmds []
          :args ["user"]}
         (parser/subcommands+args test-deep-command-tree
                                  :entry
                                  :login
                                  ["user"])))
  (is (= {:subcmds []
          :args ["user" "world"]}
         (parser/subcommands+args test-deep-command-tree
                                  :entry
                                  :login
                                  ["user" "world"])))
  (is (= {:subcmds []
          :args []}
         (parser/subcommands+args test-deep-command-tree
                                  :test-shell
                                  :cmd1
                                  [])))
  (is (= {:subcmds []
          :args ["arg1"]}
         (parser/subcommands+args test-deep-command-tree
                                  :test-shell
                                  :cmd2
                                  ["arg1"])))
  (is (= {:subcmds [:scmd1]
          :args ["arg1" "arg2"]}
         (parser/subcommands+args test-deep-command-tree
                                  :test-shell
                                  :cmd2
                                  ["scmd1" "arg1" "arg2"])))
  (is (= {:data :for-ssscmd-2}
         (grammar/get-in-command test-deep-command-tree
                                 :test-shell
                                 [:cmd2 "scmd1" "sscmd3" "ssscmd2"])))
  (is (= {:subcmds [:scmd1 :sscmd3 :ssscmd2]
          :args ["arg1" "arg2" "args3"]}
         (parser/subcommands+args test-deep-command-tree
                                  :test-shell
                                  :cmd2
                                  ["scmd1" "sscmd3" "ssscmd2" "arg1" "arg2" "args3"]))))

(deftest tokenize
  (is (= ["test-shell"]
         (parser/tokenize "test-shell")))
  (is (= ["test-shell" "cmd1"]
         (parser/tokenize "test-shell cmd1")))
  (is (= ["test-shell" "cmd1" "arg"]
         (parser/tokenize "test-shell cmd1 arg")))
  (is (= ["test-shell" "cmd2" "scmd1" "arg1" "arg2"]
         (parser/tokenize "test-shell cmd2 scmd1 arg1 arg2"))))

(deftest parse
  (is (= (parser/map->Parsed
          {:shell :test-shell
           :cmd nil
           :subcmds []
           :args []})
         (parser/parse test-deep-command-tree
                       "test-shell")))
  (is (= (parser/map->Parsed
          {:shell :test-shell
           :cmd :cmd1
           :subcmds []
           :args []})
         (parser/parse test-deep-command-tree
                       "test-shell cmd1")))
  (is (= (parser/map->Parsed
          {:shell :test-shell
           :cmd :cmd1
           :subcmds []
           :args ["arg"]})
         (parser/parse test-deep-command-tree
                       "test-shell cmd1 arg")))
  (is (= (parser/map->Parsed
          {:shell :test-shell
           :cmd :cmd2
           :subcmds [:scmd1]
           :args ["arg1" "arg2"]})
         (parser/parse test-deep-command-tree
                       "test-shell cmd2 scmd1 arg1 arg2"))))
