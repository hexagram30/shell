(ns hxgm30.shell.parser-test
  (:require
    [clojure.string :as string]
    [clojure.test :refer :all]
    [hxgm30.shell.grammar :as grammar]
    [hxgm30.shell.parser :as parser]))

(def test-command-tree1
  {"" true
   "logout" true
   "say" {:depth 1
          :any true}})

(def test-grammars
  {:tree1 test-command-tree1})

(alter-var-root #'grammar/*grammars*
                (constantly test-grammars))

(deftest line->words
  (is (= ["ps"] (parser/line->words "ps")))
  (is (= ["ps" "aux"] (parser/line->words "ps aux")))
  (is (= ["ls" "-al" "/tmp"] (parser/line->words "ls -al /tmp"))))

(deftest parse-true
  (let [result (parser/parse :tree1 "logout" "")]
    (is (= "" (:command result)))
    (is (string/starts-with? (:result-tmpl result) "Please type something")))
  (let [result (parser/parse :tree1 "logout" "what the ...")]
    (is (= "what" (:command result)))
    (is (string/starts-with? (:result-tmpl result) "Error:")))
  (let [result (parser/parse :tree1
                             "logout"
                             "say I put on my robe and wizard hat.")]
    (is (= "say" (:command result)))
    (is (= "You say: 'I put on my robe and wizard hat.'\n"
           (format (:result-tmpl result) (:result-args result)))))
  (let [result (parser/parse :tree1 "logout" "logout")]
    (is (= "logout" (:command result)))
    (is (string/starts-with? (:result-tmpl result) "Good-bye"))))
