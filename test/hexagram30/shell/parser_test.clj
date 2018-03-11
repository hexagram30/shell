(ns hexagram30.shell.parser-test
  (:require
    [clojure.string :as string]
    [clojure.test :refer :all]
    [hexagram30.shell.grammar-test :as grammar-test]
    [hexagram30.shell.parser :as parser]))

(deftest line->words
  (is (= ["ps"] (parser/line->words "ps")))
  (is (= ["ps" "aux"] (parser/line->words "ps aux")))
  (is (= ["ls" "-al" "/tmp"] (parser/line->words "ls -al /tmp"))))

(deftest parse-true
  (let [result (parser/parse "")]
    (is (= "" (:command result)))
    (is (string/starts-with? (:result-tmpl result) "Please type something")))
  (let [result (parser/parse "what the ...")]
    (is (= "what" (:command result)))
    (is (string/starts-with? (:result-tmpl result) "Error:")))
  (let [result (parser/parse "say I put on my robe and wizard hat.")]
    (is (= "say" (:command result)))
    (is (= "You say: 'I put on my robe and wizard hat.'"
           (format (:result-tmpl result) (:result-args result)))))
  (let [result (parser/parse "bye")]
    (is (= "bye" (:command result)))
    (is (string/starts-with? (:result-tmpl result) "Good-bye"))))
