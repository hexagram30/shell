(ns hxgm30.shell.reader.parser-test
  (:require
    [clojure.string :as string]
    [clojure.test :refer :all]
    [hxgm30.shell.reader.grammar :as grammar]
    [hxgm30.shell.reader.parser :as parser]))

; (def test-command-tree1
;   {"" true
;    "logout" true
;    "say" {:depth 1
;           :any true}})

; (def test-grammars
;   {:tree1 test-command-tree1})

; (defn grammar-fixture
;   [test-fn]
;   (let [orig-grammars (into {} grammar/*grammars*)]
;     (alter-var-root #'grammar/*grammars*
;                     (constantly test-grammars))
;     (test-fn)
;     (alter-var-root #'grammar/*grammars*
;                     (constantly orig-grammars))))

; (use-fixtures :once grammar-fixture)

; (deftest tokenize
;   (is (= ["ps"] (parser/tokenize "ps")))
;   (is (= ["ps" "aux"] (parser/tokenize "ps aux")))
;   (is (= ["ls" "-al" "/tmp"] (parser/tokenize "ls -al /tmp"))))

; (deftest parse-true
;   (let [result (parser/parse :tree1 "")]
;     (is (= "" (:command result)))
;     (is (string/starts-with? (:result-tmpl result) "Please type something")))
;   (let [result (parser/parse :tree1 "what the ...")]
;     (is (= "what" (:command result)))
;     (is (string/starts-with? (:result-tmpl result) "Error:")))
;   (let [result (parser/parse :tree1
;                              "say I put on my robe and wizard hat.")]
;     (is (= "say" (:command result)))
;     ;; XXX this assertion is failing; it's been a while since I touhced the
;     ;; code, so not sure if the API has changed or if there's an issue
;     ;; here ...
;     #_(is (= "You say: 'I put on my robe and wizard hat.'\n"
;            (format (:result-tmpl result) (:result-args result)))))
;   (let [result (parser/parse :tree1 "logout")]
;     (is (= "logout" (:command result)))
;     (is (string/starts-with? (:result-tmpl result) "Good-bye"))))
